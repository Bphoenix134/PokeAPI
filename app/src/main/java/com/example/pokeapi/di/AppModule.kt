package com.example.pokeapi.di

import android.content.Context
import com.example.pokeapi.data.local.PokemonDao
import com.example.pokeapi.data.remote.PokeApi
import com.example.pokeapi.data.repository.PokemonRepositoryImpl
import com.example.pokeapi.domain.repository.PokemonRepository
import com.example.pokeapi.domain.usecase.FilterPokemonUseCase
import com.example.pokeapi.domain.usecase.GetPokemonDetailUseCase
import com.example.pokeapi.domain.usecase.GetPokemonListUseCase
import com.example.pokeapi.domain.usecase.GetTypesUseCase
import com.example.pokeapi.domain.usecase.SearchPokemonUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePokeRepository(
        pokeApi: PokeApi,
        dao: PokemonDao,
        @ApplicationContext context: Context
    ): PokemonRepository {
        return PokemonRepositoryImpl(pokeApi, dao, context)
    }

    @Provides
    @Singleton
    fun provideGetPokemonListUseCase(repository: PokemonRepository): GetPokemonListUseCase {
        return GetPokemonListUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPokemonDetailUseCase(repository: PokemonRepository): GetPokemonDetailUseCase {
        return GetPokemonDetailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchPokemonUseCase(repository: PokemonRepository): SearchPokemonUseCase {
        return SearchPokemonUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFilterPokemonUseCase(repository: PokemonRepository): FilterPokemonUseCase {
        return FilterPokemonUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTypesUseCase(repository: PokemonRepository): GetTypesUseCase {
        return GetTypesUseCase(repository)
    }
}