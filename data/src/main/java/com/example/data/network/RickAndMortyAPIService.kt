package com.example.data.network

import com.example.data.entities.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyAPIService {
    @GET("character")
    suspend fun getCharacters(): CharacterList?

    @GET("character/{characterId}")
    suspend fun getCharacter(@Path("characterId") characterId: Int): Character?

    @GET("character")
    suspend fun filterCharacters(
        @Query("name")
        name: String,
        @Query("status")
        status: String,
        @Query("species")
        species: String,
        @Query("type")
        type: String,
        @Query("gender")
        gender: String
    ): CharacterList?

    @GET("character")
    suspend fun getCharacters(
        @Query("page")
        pageNumber: Int,
        @Query("name")
        name: String,
        @Query("status")
        status: String,
        @Query("species")
        species: String,
        @Query("type")
        type: String,
        @Query("gender")
        gender: String
    ): CharacterList?


    @GET("location")
    suspend fun getLocations(): LocationList?

    @GET("location/{locationId}")
    suspend fun getLocation(
        @Path("locationId")
        locationId: Int
    ): Location?

    @GET("location")
    suspend fun filterLocations(
        @Query("name")
        name: String,
        @Query("type")
        type: String,
        @Query("dimension")
        dimension: String
    ): LocationList?

    @GET("location")
    suspend fun getLocations(
        @Query("page")
        pageNumber: Int,
        @Query("name")
        name: String,
        @Query("type")
        type: String,
        @Query("dimension")
        dimension: String
    ): LocationList?

    @GET("episode")
    suspend fun getEpisodes(): EpisodeList?

    @GET("episode/{episodeId}")
    suspend fun getEpisode(
        @Path("episodeId")
        episodeId: Int
    ): Episode?

    @GET("episode")
    suspend fun filterEpisodes(
        @Query("name")
        name: String,
        @Query("episode")
        episode: String
    ): EpisodeList?

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page")
        pageNumber: Int,
        @Query("name")
        name: String,
        @Query("episode")
        episode: String
    ): EpisodeList?
}