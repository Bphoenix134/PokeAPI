package com.example.pokeapi.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.pokeapi.data.local.PokemonDao
import com.example.pokeapi.data.local.PokemonEntity
import com.example.pokeapi.data.local.TypeEntity
import com.example.pokeapi.data.remote.PokeApi
import com.example.pokeapi.data.remote.dto.PokemonSummaryDto
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.repository.PokemonRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val pokemonDao: PokemonDao,
    @ApplicationContext private val context: Context
) : PokemonRepository {

    private var currentPagingSource: PagingSource<Int, Pokemon>? = null

    private fun createPagingSource(): PagingSource<Int, Pokemon> {
        Log.d("PokemonRepository", "Creating new PagingSource")
        return object : PagingSource<Int, Pokemon>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
                val pageOffset = params.key ?: 0
                try {
                    val isOnline = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null
                    Log.d("PokemonRepository", "Loading page with offset: $pageOffset, isOnline: $isOnline")
                    if (isOnline) {
                        val response = api.getPokemonList(params.loadSize, pageOffset)
                        Log.d("PokemonRepository", "API response: ${response.results.size} items")
                        val pokemons = response.results.map { dto: PokemonSummaryDto ->
                            Pokemon(
                                id = dto.url.extractId(),
                                name = dto.name,
                                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${dto.url.extractId()}.png"
                            )
                        }
                        pokemonDao.insertAll(pokemons.map { it.toEntity() })
                        coroutineScope {
                            pokemons.map { pokemon ->
                                async {
                                    try {
                                        val detail = getPokemonDetail(pokemon.name)
                                        pokemonDao.insert(detail.toEntity())
                                    } catch (e: Exception) {
                                        Log.e("PokemonRepository", "Failed to fetch detail for ${pokemon.name}: ${e.message}")
                                    }
                                }
                            }.awaitAll()
                        }
                        return LoadResult.Page(
                            data = pokemons,
                            prevKey = if (pageOffset == 0) null else pageOffset - params.loadSize,
                            nextKey = pageOffset + params.loadSize
                        )
                    } else {
                        val cached = pokemonDao.getPokemonList(params.loadSize, pageOffset)
                        Log.d("PokemonRepository", "Cached data: ${cached.size} items")
                        if (cached.isEmpty()) {
                            return LoadResult.Error(Exception("No data available offline"))
                        }
                        val totalCount = pokemonDao.getPokemonCount()
                        val nextKey = if (pageOffset + params.loadSize >= totalCount || cached.isEmpty()) null else pageOffset + params.loadSize
                        return LoadResult.Page(
                            data = cached.map { it.toPokemon() },
                            prevKey = if (pageOffset == 0) null else pageOffset - params.loadSize,
                            nextKey = nextKey
                        )
                    }
                } catch (e: Exception) {
                    Log.e("PokemonRepository", "Error loading Pokémon list: ${e.message}", e)
                    return LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
                }
            }
        }.also { currentPagingSource = it }
    }

    private val pager = Pager(PagingConfig(pageSize = 20)) { createPagingSource() }

    private fun String.extractId(): Int = split("/").dropLast(1).last().toInt()

    private fun Pokemon.toEntity() = PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = emptyList(),
        height = 0,
        weight = 0,
        abilities = emptyList()
    )

    private fun PokemonDetail.toEntity() = PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types,
        height = height,
        weight = weight,
        abilities = abilities
    )

    private fun PokemonEntity.toPokemon() = Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl
    )

    private fun PokemonEntity.toPokemonDetail() = PokemonDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types.filter { it.isNotBlank() },
        height = height,
        weight = weight,
        abilities = abilities.filter { it.isNotBlank() }
    )

    override fun getPokemonList(limit: Int, offset: Int): Flow<PagingData<Pokemon>> {
        Log.d("PokemonRepository", "Returning pokemonList flow")
        return pager.flow
    }

    override fun invalidate() {
        Log.d("PokemonRepository", "Invalidating PagingSource")
        currentPagingSource?.invalidate()
    }

    override suspend fun getPokemonDetail(name: String): PokemonDetail {
        val isOnline = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null
        if (isOnline) {
            try {
                val response = api.getPokemonListDetail(name)
                val detail = PokemonDetail(
                    id = response.id,
                    name = response.name,
                    imageUrl = response.sprites.front_default,
                    types = response.types.map { it.type.name },
                    height = response.height,
                    weight = response.weight,
                    abilities = response.abilities.map { it.ability.name }
                )
                pokemonDao.insert(detail.toEntity())
                return detail
            } catch (e: Exception) {
                Log.e("PokemonRepository", "Error fetching Pokémon detail for $name: ${e.message}")
            }
        }
        val cached = pokemonDao.getPokemonByName(name)
        return cached?.toPokemonDetail() ?: throw Exception("Pokemon not found")
    }

    override suspend fun searchPokemon(query: String): List<Pokemon> {
        return pokemonDao.searchPokemon("%$query%").map { it.toPokemon() }
    }

    override suspend fun filterByTypes(types: List<String>): List<Pokemon> {
        if (types.isEmpty()) return emptyList()
        val conditions = types.joinToString(" OR ") { "types LIKE '%$it%'" }
        val sql = "SELECT * FROM pokemon WHERE $conditions"
        Log.d("FilterSQL", sql)
        return pokemonDao.filterByTypes(query = SimpleSQLiteQuery(sql)).map { it.toPokemon() }
    }

    override suspend fun getTypes(): List<String> {
        val isOnline = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null
        if (isOnline) {
            try {
                val response = api.getTypes()
                val types = response.results.map { it.name }.filter { it != "unknown" }
                pokemonDao.insertTypes(types.map { TypeEntity(name = it) })
                return types
            } catch (e: Exception) {
                Log.e("PokemonRepository", "Error fetching types: ${e.message}")
            }
        }
        return pokemonDao.getAllTypes().map { it.name }
    }
}