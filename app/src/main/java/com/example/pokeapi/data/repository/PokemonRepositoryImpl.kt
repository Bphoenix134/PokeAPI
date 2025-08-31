package com.example.pokeapi.data.repository

import android.content.Context
import android.net.ConnectivityManager
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.pokeapi.data.local.PokemonDao
import com.example.pokeapi.data.local.PokemonEntity
import com.example.pokeapi.data.remote.PokeApi
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.repository.PokemonRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val pokemonDao: PokemonDao,
    @ApplicationContext private val context: Context
) : PokemonRepository {

    private fun String.extractId(): Int = split("/").dropLast(1).last().toInt()

    private fun Pokemon.toEntity() = PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = "",
        height = 0,
        weight = 0,
        abilities = ""
    )

    private fun PokemonDetail.toEntity() = PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        types = types.joinToString(","),
        height = height,
        weight = weight,
        abilities = abilities.joinToString(",")
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
        types = types.split(","),
        height = height,
        weight = weight,
        abilities = abilities.split(",")
    )

    override fun getPokemonList(limit: Int, offset: Int): Flow<PagingData<Pokemon>> {
        return Pager(PagingConfig(pageSize = limit)) {
            object : PagingSource<Int, Pokemon>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
                    try {
                        val isOnline = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null
                        if (isOnline) {
                            val response = api.getPokemonList(limit, params.key ?: 0)
                            val pokemons = response.result.map { dto ->
                                Pokemon(
                                    id = dto.url.extractId(),
                                    name = dto.name,
                                    imageUrl = "https://pokeapi.co/api/v2/pokemon/${dto.name}/sprites/front_default"
                                )
                            }
                            pokemonDao.insertAll(pokemons.map { it.toEntity() })
                            return LoadResult.Page(pokemons, null, params.key?.plus(limit))
                        } else {
                            val cached = pokemonDao.getPokemonList(limit, params.key ?: 0)
                            return LoadResult.Page(
                                data = cached.map { it.toPokemon() },
                                prevKey = null,
                                nextKey = if (cached.isEmpty()) null else params.key?.plus(limit)
                            )
                        }
                    } catch (e: Exception) {
                        return LoadResult.Error(e)
                    }
                }
            }
        }.flow
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
                // Попробуем загрузить из кэша
            }
        }
        val cached = pokemonDao.getPokemonByName(name)
        return cached?.toPokemonDetail() ?: throw Exception("Pokemon not found")
    }
}