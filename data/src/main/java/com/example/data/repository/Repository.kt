package com.example.data.repository

import com.example.data.IRepository
import com.example.data.database.RickyAndMortyDao
import com.example.data.entities.*
import com.example.data.network.RickAndMortyAPIService

class Repository(private val api: RickAndMortyAPIService, private val dao: RickyAndMortyDao) :
    IRepository {

    override suspend fun getCharacterList(): CharacterList? {
        var characterList = try {
            api.getCharacters()
        } catch (e: Exception) {
            null
        }
        if (characterList != null) {
            insertCharactersIntoDB(characterList.results)
        } else {
            characterList = dao.getCharacters()?.let {
                CharacterList(Info(0, 0, null, null), it)
            }
        }
        return characterList
    }

    override suspend fun getCharacterList(
        page: Int,
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ): CharacterList? {
        val characterList = try {
            api.getCharacters(page, name, status, species, type, gender)
        } catch (e: Exception) {
            null
        }
        if (characterList != null) {
            insertCharactersIntoDB(characterList.results)
        }
        return characterList
    }

    override suspend fun filterCharacters(
        name: String, status: String, species: String, type: String, gender: String
    ): CharacterList? {
        var characterList = try {
            api.filterCharacters(name, status, species, type, gender)
        } catch (e: Exception) {
            null
        }
        if (characterList != null) {
            insertCharactersIntoDB(characterList.results)
        } else {
            characterList = dao.filterCharacters(
                "%$name%",
                "%$status%",
                "%$species%",
                "%$type%",
                "%$gender%"
            )?.let {
                CharacterList(Info(0, 0, null, null), it)
            }
        }
        return characterList
    }

    private suspend fun insertCharactersIntoDB(characters: List<Character>) {
        characters.forEach { dao.getCharacter(it.id) ?: dao.insertCharacter(it) }
    }

    override suspend fun getCharacter(id: Int): Character? = dao.getCharacter(id) ?: try {
        val character = api.getCharacter(id)
        if (character != null) {
            dao.insertCharacter(character)
        }
        character
    } catch (e: Exception) {
        null
    }

    override suspend fun getLocationList(): LocationList? {
        var locationList = try {
            api.getLocations()
        } catch (e: Exception) {
            null
        }
        if (locationList != null) {
            insertLocationsIntoDB(locationList.results)
        } else {
            locationList = dao.getLocations()?.let {
                LocationList(Info(0, 0, null, null), it)
            }
        }
        return locationList
    }

    override suspend fun getLocationList(
        page: Int,
        name: String,
        type: String,
        dimension: String
    ): LocationList? {
        val locationList = try {
            api.getLocations(page, name, type, dimension)
        } catch (e: Exception) {
            null
        }
        if (locationList != null) {
            insertLocationsIntoDB(locationList.results)
        }
        return locationList
    }

    override suspend fun filterLocations(
        name: String,
        type: String,
        dimension: String
    ): LocationList? {
        var locationList = try {
            api.filterLocations(name, type, dimension)
        } catch (e: Exception) {
            null
        }

        if (locationList != null) {
            insertLocationsIntoDB(locationList.results)
        } else {
            locationList = dao.filterLocations(
                "%$name%",
                "%$type%",
                "%$dimension%"
            )?.let {
                LocationList(Info(0, 0, null, null), it)
            }
        }
        return locationList
    }

    private suspend fun insertLocationsIntoDB(locations: List<Location>) {
        locations.forEach { dao.getLocation(it.id) ?: dao.insertLocation(it) }
    }

    override suspend fun getLocation(id: Int): Location? = dao.getLocation(id) ?: try {
        val location = api.getLocation(id)
        if (location != null) {
            dao.insertLocation(location)
        }
        location
    } catch (e: Exception) {
        null
    }

    override suspend fun getEpisodeList(): EpisodeList? {
        var episodeList = try {
            api.getEpisodes()
        } catch (e: Exception) {
            null
        }
        if (episodeList != null) {
            insertEpisodesIntoDB(episodeList.results)
        } else {
            episodeList = dao.getEpisodes()?.let {
                EpisodeList(Info(0, 0, null, null), it)
            }
        }
        return episodeList
    }

    override suspend fun getEpisodeList(page: Int, name: String, episode: String): EpisodeList? {
        val episodeList = try {
            api.getEpisodes(page, name, episode)
        } catch (e: Exception) {
            null
        }
        if (episodeList != null) {
            insertEpisodesIntoDB(episodeList.results)
        }
        return episodeList
    }

    override suspend fun filterEpisodes(name: String, episode: String): EpisodeList? {
        var episodeList = try {
            api.filterEpisodes(name, episode)
        } catch (e: Exception) {
            null
        }

        if (episodeList != null) {
            insertEpisodesIntoDB(episodeList.results)
        } else {
            episodeList = dao.filterEpisodes(
                "%$name%",
                "%$episode%"
            )?.let {
                EpisodeList(Info(0, 0, null, null), it)
            }
        }
        return episodeList
    }

    private suspend fun insertEpisodesIntoDB(episodes: List<Episode>) {
        episodes.forEach { dao.getEpisode(it.id) ?: dao.insertEpisode(it) }
    }

    override suspend fun getEpisode(id: Int): Episode? = dao.getEpisode(id) ?: try {
        val episode = api.getEpisode(id)
        if (episode != null) {
            dao.insertEpisode(episode)
        }
        episode
    } catch (e: Exception) {
        null
    }
}