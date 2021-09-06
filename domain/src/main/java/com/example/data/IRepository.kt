package com.example.data

import com.example.data.entities.*

interface IRepository {
    suspend fun getCharacterList(): CharacterList?

    suspend fun getCharacterList(
        page: Int,
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ): CharacterList?

    suspend fun filterCharacters(
        name: String, status: String, species: String, type: String, gender: String
    ): CharacterList?

    suspend fun getCharacter(id: Int): Character?

    suspend fun getLocationList(): LocationList?

    suspend fun getLocationList(
        page: Int,
        name: String,
        type: String,
        dimension: String
    ): LocationList?

    suspend fun filterLocations(name: String, type: String, dimension: String): LocationList?

    suspend fun getLocation(id: Int): Location?

    suspend fun getEpisodeList(): EpisodeList?

    suspend fun getEpisodeList(page: Int, name: String, episode: String): EpisodeList?

    suspend fun filterEpisodes(name: String, episode: String): EpisodeList?

    suspend fun getEpisode(id: Int): Episode?
}