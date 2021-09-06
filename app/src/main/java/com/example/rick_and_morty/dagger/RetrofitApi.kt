package com.example.rick_and_morty.dagger

import com.example.data.network.RickAndMortyAPIService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class RetrofitApi {

    @Singleton
    @Provides
    fun rickAndMortyAPIService(retrofit: Retrofit): RickAndMortyAPIService =
        retrofit.create(RickAndMortyAPIService::class.java)

    @Provides
    fun retrofit(moshiConverterFactory: MoshiConverterFactory): Retrofit = Retrofit.Builder()
        .addConverterFactory(moshiConverterFactory)
        .baseUrl(BASE_URL)
        .build()

    @Provides
    fun moshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    fun moshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}