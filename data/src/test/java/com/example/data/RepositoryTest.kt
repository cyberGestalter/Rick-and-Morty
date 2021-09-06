package com.example.data

import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_GENDER
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_ID
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_NAME
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_PAGE
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_SPECIES
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_STATUS
import com.example.data.RepositoryTest.TestCharacterParameters.CHARACTER_TYPE
import com.example.data.RepositoryTest.TestCharacterParameters.character
import com.example.data.RepositoryTest.TestCharacterParameters.characterList
import com.example.data.RepositoryTest.TestCharacterParameters.characters
import com.example.data.RepositoryTest.TestEpisodeParameters.EPISODE_ID
import com.example.data.RepositoryTest.TestEpisodeParameters.EPISODE_NAME
import com.example.data.RepositoryTest.TestEpisodeParameters.EPISODE_PAGE
import com.example.data.RepositoryTest.TestEpisodeParameters.EPISODE_SYMBOL
import com.example.data.RepositoryTest.TestEpisodeParameters.episode
import com.example.data.RepositoryTest.TestEpisodeParameters.episodeList
import com.example.data.RepositoryTest.TestEpisodeParameters.episodes
import com.example.data.RepositoryTest.TestLocationParameters.LOCATION_DIMENSION
import com.example.data.RepositoryTest.TestLocationParameters.LOCATION_ID
import com.example.data.RepositoryTest.TestLocationParameters.LOCATION_NAME
import com.example.data.RepositoryTest.TestLocationParameters.LOCATION_PAGE
import com.example.data.RepositoryTest.TestLocationParameters.LOCATION_TYPE
import com.example.data.RepositoryTest.TestLocationParameters.location
import com.example.data.RepositoryTest.TestLocationParameters.locationList
import com.example.data.RepositoryTest.TestLocationParameters.locations
import com.example.data.database.RickyAndMortyDao
import com.example.data.entities.*
import com.example.data.network.RickAndMortyAPIService
import com.example.data.repository.Repository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    @MockK
    private lateinit var api: RickAndMortyAPIService

    @MockK
    private lateinit var dao: RickyAndMortyDao

    @InjectMockKs
    private lateinit var repository: Repository

    @Before
    fun initMocks() = MockKAnnotations.init(this)

    @After
    fun makeLastCheckOnExitTest() = confirmVerified(api, dao)

    object TestCharacterParameters {
        const val CHARACTER_ID = 1
        const val CHARACTER_PAGE = 2
        const val CHARACTER_NAME = "Morty"
        const val CHARACTER_STATUS = "Alive"
        const val CHARACTER_SPECIES = "Human"
        const val CHARACTER_TYPE = ""
        const val CHARACTER_GENDER = "Male"

        val character = Character(
            CHARACTER_ID,
            CHARACTER_NAME,
            CHARACTER_STATUS,
            CHARACTER_SPECIES,
            CHARACTER_TYPE,
            CHARACTER_GENDER,
            CharacterLocation(),
            CharacterLocation(),
            "",
            listOf(),
            "",
            ""
        )
        val characters = listOf(character)
        val characterList = CharacterList(Info(0, 0, null, null), characters)
    }

    object TestLocationParameters {
        const val LOCATION_ID = 1
        const val LOCATION_PAGE = 2
        const val LOCATION_NAME = "Gaia"
        const val LOCATION_TYPE = "Planet"
        const val LOCATION_DIMENSION = "Dimension"

        val location = Location(
            LOCATION_ID, LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION, listOf(), "", ""
        )
        val locations = listOf(location)
        val locationList = LocationList(Info(0, 0, null, null), locations)
    }

    object TestEpisodeParameters {
        const val EPISODE_ID = 1
        const val EPISODE_PAGE = 2
        const val EPISODE_NAME = "Pilot"
        private const val EPISODE_AIR_DATE = "airDate"
        const val EPISODE_SYMBOL = "S01E01"

        val episode = Episode(
            EPISODE_ID, EPISODE_NAME, EPISODE_AIR_DATE, EPISODE_SYMBOL, listOf(), "", ""
        )
        val episodes = listOf(episode)
        val episodeList = EpisodeList(Info(0, 0, null, null), episodes)
    }

    @Test
    fun getCharacterList_returns_charactersFromServer_when_noNetworkError() = runBlocking {
        coEvery { api.getCharacters() } returns characterList
        coEvery { dao.getCharacter(CHARACTER_ID) } returns character

        assertEquals(characterList, repository.getCharacterList())
        coVerify {
            api.getCharacters()
            dao.getCharacter(CHARACTER_ID)
        }
    }

    @Test
    fun getCharacterList_saves_charactersFromServer_when_noNetworkError_and_noCharactersInDB() =
        runBlocking {
            coEvery { api.getCharacters() } returns characterList
            coEvery { dao.getCharacter(CHARACTER_ID) } returns null
            coEvery { dao.insertCharacter(character) } just Runs

            repository.getCharacterList()

            coVerify {
                api.getCharacters()
                dao.getCharacter(CHARACTER_ID)
                dao.insertCharacter(character)
            }
        }

    @Test
    fun getCharacterList_returns_charactersFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery { api.getCharacters() } throws Exception()
        coEvery { dao.getCharacters() } returns characters

        assertEquals(characterList, repository.getCharacterList())
        coVerify {
            api.getCharacters()
            dao.getCharacters()
        }
    }

    @Test
    fun getCharacterList_returns_null_when_networkErrorOccurs_and_dBHasNoCharactersNeeded() =
        runBlocking {
            coEvery { api.getCharacters() } throws Exception()
            coEvery { dao.getCharacters() } returns null

            assertEquals(null, repository.getCharacterList())
            coVerify {
                api.getCharacters()
                dao.getCharacters()
            }
        }

    @Test
    fun pagedGetCharacterList_returns_charactersOnlyFromServer() = runBlocking {
        coEvery {
            api.getCharacters(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        } returns
                characterList
        coEvery { dao.getCharacter(CHARACTER_ID) } returns character

        assertEquals(
            characterList, repository
                .getCharacterList(
                    CHARACTER_PAGE,
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
        )
        coVerify {
            api.getCharacters(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            dao.getCharacter(CHARACTER_ID)
        }
    }

    @Test
    fun pagedGetCharacterList_saves_characters_when_noNetworkError_and_noCharactersInDB() =
        runBlocking {
            coEvery {
                api.getCharacters(
                    CHARACTER_PAGE,
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
            } returns
                    characterList
            coEvery { dao.getCharacter(CHARACTER_ID) } returns null
            coEvery { dao.insertCharacter(character) } just Runs

            repository.getCharacterList(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            coVerify {
                api.getCharacters(
                    CHARACTER_PAGE,
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
                dao.getCharacter(CHARACTER_ID)
                dao.insertCharacter(character)
            }
        }

    @Test
    fun pagedGetCharacterList_returns_null_when_networkErrorOccurs() = runBlocking {
        coEvery {
            api.getCharacters(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        } throws Exception()

        assertEquals(
            null,
            repository.getCharacterList(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        )
        coVerify {
            api.getCharacters(
                CHARACTER_PAGE,
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            dao wasNot Called
        }
    }

    @Test
    fun filterCharacters_returns_charactersFromServer_when_noNetworkError() = runBlocking {
        coEvery {
            api.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        } returns characterList
        coEvery { dao.getCharacter(CHARACTER_ID) } returns character

        assertEquals(
            characterList,
            repository.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        )
        coVerify {
            api.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            dao.getCharacter(CHARACTER_ID)
        }
    }

    @Test
    fun filterCharacters_saves_charactersFromServer_when_noNetworkError_and_noCharactersInDB() =
        runBlocking {
            coEvery {
                api.filterCharacters(
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
            } returns
                    characterList
            coEvery { dao.getCharacter(CHARACTER_ID) } returns null
            coEvery { dao.insertCharacter(character) } just Runs

            repository.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            coVerify {
                api.filterCharacters(
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
                dao.getCharacter(CHARACTER_ID)
                dao.insertCharacter(character)
            }
        }

    @Test
    fun filterCharacters_returns_charactersFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery {
            api.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        } throws Exception()
        coEvery {
            dao.filterCharacters(
                "%$CHARACTER_NAME%",
                "%$CHARACTER_STATUS%",
                "%$CHARACTER_SPECIES%",
                "%$CHARACTER_TYPE%",
                "%$CHARACTER_GENDER%"
            )
        } returns characters

        assertEquals(
            characterList,
            repository.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
        )
        coVerify {
            api.filterCharacters(
                CHARACTER_NAME,
                CHARACTER_STATUS,
                CHARACTER_SPECIES,
                CHARACTER_TYPE,
                CHARACTER_GENDER
            )
            dao.filterCharacters(
                "%$CHARACTER_NAME%",
                "%$CHARACTER_STATUS%",
                "%$CHARACTER_SPECIES%",
                "%$CHARACTER_TYPE%",
                "%$CHARACTER_GENDER%"
            )
        }
    }

    @Test
    fun filterCharacters_returns_null_when_networkErrorOccurs_and_dBHasNoCharactersNeeded() =
        runBlocking {
            coEvery {
                api.filterCharacters(
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
            } throws Exception()
            coEvery {
                dao.filterCharacters(
                    "%$CHARACTER_NAME%",
                    "%$CHARACTER_STATUS%",
                    "%$CHARACTER_SPECIES%",
                    "%$CHARACTER_TYPE%",
                    "%$CHARACTER_GENDER%"
                )
            } returns null

            assertEquals(
                null,
                repository.filterCharacters(
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
            )
            coVerify {
                api.filterCharacters(
                    CHARACTER_NAME,
                    CHARACTER_STATUS,
                    CHARACTER_SPECIES,
                    CHARACTER_TYPE,
                    CHARACTER_GENDER
                )
                dao.filterCharacters(
                    "%$CHARACTER_NAME%",
                    "%$CHARACTER_STATUS%",
                    "%$CHARACTER_SPECIES%",
                    "%$CHARACTER_TYPE%",
                    "%$CHARACTER_GENDER%"
                )
            }
        }

    @Test
    fun getCharacter_returns_characterFromDB_when_dBHasThisCharacter() = runBlocking {
        coEvery { dao.getCharacter(CHARACTER_ID) } returns character
        assertEquals(character, repository.getCharacter(CHARACTER_ID))
        coVerify {
            dao.getCharacter(CHARACTER_ID)
            api wasNot Called
        }
    }

    @Test
    fun getCharacter_returns_characterFromServer_when_dBHasNoThisCharacter_andSaves_itIntoDB() =
        runBlocking {
            coEvery { dao.getCharacter(CHARACTER_ID) } returns null
            coEvery { api.getCharacter(CHARACTER_ID) } returns character
            coEvery { dao.insertCharacter(character) } just Runs

            assertEquals(character, repository.getCharacter(CHARACTER_ID))
            coVerify {
                dao.getCharacter(CHARACTER_ID)
                api.getCharacter(CHARACTER_ID)
                dao.insertCharacter(character)
            }
        }

    @Test
    fun getCharacter_returns_null_when_noCharacterInDBAndErrorNetworkOccurs() = runBlocking {
        coEvery { dao.getCharacter(CHARACTER_ID) } returns null
        coEvery { api.getCharacter(CHARACTER_ID) } throws Exception()

        assertEquals(null, repository.getCharacter(CHARACTER_ID))
        coVerify {
            dao.getCharacter(CHARACTER_ID)
            api.getCharacter(CHARACTER_ID)
        }
    }

    @Test
    fun getLocationList_returns_locationsFromServer_when_noNetworkError() = runBlocking {
        coEvery { api.getLocations() } returns locationList
        coEvery { dao.getLocation(LOCATION_ID) } returns location

        assertEquals(locationList, repository.getLocationList())
        coVerify {
            api.getLocations()
            dao.getLocation(LOCATION_ID)
        }
    }

    @Test
    fun getLocationList_saves_locationsFromServer_when_noNetworkError_and_noCharactersInDB() =
        runBlocking {
            coEvery { api.getLocations() } returns locationList
            coEvery { dao.getLocation(LOCATION_ID) } returns null
            coEvery { dao.insertLocation(location) } just Runs

            repository.getLocationList()

            coVerify {
                api.getLocations()
                dao.getLocation(LOCATION_ID)
                dao.insertLocation(location)
            }
        }

    @Test
    fun getLocationList_returns_locationsFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery { api.getLocations() } throws Exception()
        coEvery { dao.getLocations() } returns locations

        assertEquals(locationList, repository.getLocationList())
        coVerify {
            api.getLocations()
            dao.getLocations()
        }
    }

    @Test
    fun getLocationList_returns_null_when_networkErrorOccurs_and_dBHasNoLocationsNeeded() =
        runBlocking {
            coEvery { api.getLocations() } throws Exception()
            coEvery { dao.getLocations() } returns null

            assertEquals(null, repository.getLocationList())
            coVerify {
                api.getLocations()
                dao.getLocations()
            }
        }

    @Test
    fun pagedGetLocationList_returns_locationsOnlyFromServer() = runBlocking {
        coEvery {
            api.getLocations(
                LOCATION_PAGE,
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        } returns locationList
        coEvery { dao.getLocation(LOCATION_ID) } returns location

        assertEquals(
            locationList,
            repository.getLocationList(
                LOCATION_PAGE,
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        )
        coVerify {
            api.getLocations(LOCATION_PAGE, LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            dao.getLocation(LOCATION_ID)
        }
    }

    @Test
    fun pagedGetLocationList_saves_locations_when_noNetworkError_and_noLocationsInDB() =
        runBlocking {
            coEvery {
                api.getLocations(
                    LOCATION_PAGE,
                    LOCATION_NAME,
                    LOCATION_TYPE,
                    LOCATION_DIMENSION
                )
            } returns locationList
            coEvery { dao.getLocation(LOCATION_ID) } returns null
            coEvery { dao.insertLocation(location) } just Runs

            repository.getLocationList(
                LOCATION_PAGE,
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
            coVerify {
                api.getLocations(LOCATION_PAGE, LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
                dao.getLocation(LOCATION_ID)
                dao.insertLocation(location)
            }
        }

    @Test
    fun pagedGetLocationList_returns_null_when_networkErrorOccurs() = runBlocking {
        coEvery {
            api.getLocations(
                LOCATION_PAGE,
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        } throws Exception()

        assertEquals(
            null,
            repository.getLocationList(
                LOCATION_PAGE,
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        )
        coVerify {
            api.getLocations(LOCATION_PAGE, LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            dao wasNot Called
        }
    }

    @Test
    fun filterLocations_returns_locationsFromServer_when_noNetworkError() = runBlocking {
        coEvery {
            api.filterLocations(
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        } returns locationList
        coEvery { dao.getLocation(LOCATION_ID) } returns location

        assertEquals(
            locationList,
            repository.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
        )
        coVerify {
            api.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            dao.getLocation(LOCATION_ID)
        }
    }

    @Test
    fun filterLocations_saves_locationsFromServer_when_noNetworkError_and_noLocationsInDB() =
        runBlocking {
            coEvery {
                api.filterLocations(
                    LOCATION_NAME,
                    LOCATION_TYPE,
                    LOCATION_DIMENSION
                )
            } returns locationList
            coEvery { dao.getLocation(LOCATION_ID) } returns null
            coEvery { dao.insertLocation(location) } just Runs

            repository.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            coVerify {
                api.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
                dao.getLocation(LOCATION_ID)
                dao.insertLocation(location)
            }
        }

    @Test
    fun filterLocations_returns_locationsFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery {
            api.filterLocations(
                LOCATION_NAME,
                LOCATION_TYPE,
                LOCATION_DIMENSION
            )
        } throws Exception()
        coEvery {
            dao.filterLocations("%$LOCATION_NAME%", "%$LOCATION_TYPE%", "%$LOCATION_DIMENSION%")
        } returns locations

        assertEquals(
            locationList,
            repository.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
        )
        coVerify {
            api.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            dao.filterLocations("%$LOCATION_NAME%", "%$LOCATION_TYPE%", "%$LOCATION_DIMENSION%")
        }
    }

    @Test
    fun filterLocations_returns_null_when_networkErrorOccurs_and_dBHasNoLocationsNeeded() =
        runBlocking {
            coEvery {
                api.filterLocations(
                    LOCATION_NAME,
                    LOCATION_TYPE,
                    LOCATION_DIMENSION
                )
            } throws Exception()
            coEvery {
                dao.filterLocations("%$LOCATION_NAME%", "%$LOCATION_TYPE%", "%$LOCATION_DIMENSION%")
            } returns null

            assertEquals(
                null,
                repository.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
            )
            coVerify {
                api.filterLocations(LOCATION_NAME, LOCATION_TYPE, LOCATION_DIMENSION)
                dao.filterLocations("%$LOCATION_NAME%", "%$LOCATION_TYPE%", "%$LOCATION_DIMENSION%")
            }
        }

    @Test
    fun getLocation_returns_locationFromDB_when_dBHasThisLocation() = runBlocking {
        coEvery { dao.getLocation(LOCATION_ID) } returns location
        assertEquals(location, repository.getLocation(LOCATION_ID))
        coVerify {
            dao.getLocation(LOCATION_ID)
            api wasNot Called
        }
    }

    @Test
    fun getLocation_returns_locationFromServer_when_dBHasNoThisLocation_andSaves_itIntoDB() =
        runBlocking {
            coEvery { dao.getLocation(LOCATION_ID) } returns null
            coEvery { api.getLocation(LOCATION_ID) } returns location
            coEvery { dao.insertLocation(location) } just Runs

            assertEquals(location, repository.getLocation(LOCATION_ID))
            coVerify {
                dao.getLocation(LOCATION_ID)
                api.getLocation(LOCATION_ID)
                dao.insertLocation(location)
            }
        }

    @Test
    fun getLocation_returns_null_when_noLocationInDBAndErrorNetworkOccurs() = runBlocking {
        coEvery { dao.getLocation(LOCATION_ID) } returns null
        coEvery { api.getLocation(LOCATION_ID) } throws Exception()

        assertEquals(null, repository.getLocation(LOCATION_ID))
        coVerify {
            dao.getLocation(LOCATION_ID)
            api.getLocation(LOCATION_ID)
        }
    }

    @Test
    fun getEpisodeList_returns_episodesFromServer_when_noNetworkError() = runBlocking {
        coEvery { api.getEpisodes() } returns episodeList
        coEvery { dao.getEpisode(EPISODE_ID) } returns episode

        assertEquals(episodeList, repository.getEpisodeList())
        coVerify {
            api.getEpisodes()
            dao.getEpisode(EPISODE_ID)
        }
    }

    @Test
    fun getEpisodeList_saves_episodesFromServer_when_noNetworkError_and_noEpisodesInDB() =
        runBlocking {
            coEvery { api.getEpisodes() } returns episodeList
            coEvery { dao.getEpisode(EPISODE_ID) } returns null
            coEvery { dao.insertEpisode(episode) } just Runs

            repository.getEpisodeList()

            coVerify {
                api.getEpisodes()
                dao.getEpisode(EPISODE_ID)
                dao.insertEpisode(episode)
            }
        }

    @Test
    fun getEpisodeList_returns_episodesFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery { api.getEpisodes() } throws Exception()
        coEvery { dao.getEpisodes() } returns episodes

        assertEquals(episodeList, repository.getEpisodeList())
        coVerify {
            api.getEpisodes()
            dao.getEpisodes()
        }
    }

    @Test
    fun getEpisodeList_returns_null_when_networkErrorOccurs_and_dBHasNoEpisodesNeeded() =
        runBlocking {
            coEvery { api.getEpisodes() } throws Exception()
            coEvery { dao.getEpisodes() } returns null

            assertEquals(null, repository.getEpisodeList())
            coVerify {
                api.getEpisodes()
                dao.getEpisodes()
            }
        }

    @Test
    fun pagedGetEpisodeList_returns_episodesOnlyFromServer() = runBlocking {
        coEvery { api.getEpisodes(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL) } returns episodeList
        coEvery { dao.getEpisode(EPISODE_ID) } returns episode

        assertEquals(
            episodeList,
            repository.getEpisodeList(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL)
        )
        coVerify {
            api.getEpisodes(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL)
            dao.getEpisode(EPISODE_ID)
        }
    }

    @Test
    fun pagedGetEpisodeList_saves_episodes_when_noNetworkError_and_noEpisodesInDB() =
        runBlocking {
            coEvery {
                api.getEpisodes(
                    EPISODE_PAGE,
                    EPISODE_NAME,
                    EPISODE_SYMBOL
                )
            } returns episodeList
            coEvery { dao.getEpisode(EPISODE_ID) } returns null
            coEvery { dao.insertEpisode(episode) } just Runs

            repository.getEpisodeList(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL)
            coVerify {
                api.getEpisodes(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL)
                dao.getEpisode(EPISODE_ID)
                dao.insertEpisode(episode)
            }
        }

    @Test
    fun pagedGetEpisodeList_returns_null_when_networkErrorOccurs() = runBlocking {
        coEvery { api.getEpisodes(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL) } throws Exception()

        assertEquals(null, repository.getEpisodeList(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL))
        coVerify {
            api.getEpisodes(EPISODE_PAGE, EPISODE_NAME, EPISODE_SYMBOL)
            dao wasNot Called
        }
    }

    @Test
    fun filterEpisodes_returns_episodesFromServer_when_noNetworkError() = runBlocking {
        coEvery { api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL) } returns episodeList
        coEvery { dao.getEpisode(EPISODE_ID) } returns episode

        assertEquals(episodeList, repository.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL))
        coVerify {
            api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL)
            dao.getEpisode(EPISODE_ID)
        }
    }

    @Test
    fun filterEpisodes_saves_episodesFromServer_when_noNetworkError_and_noEpisodesInDB() =
        runBlocking {
            coEvery { api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL) } returns episodeList
            coEvery { dao.getEpisode(EPISODE_ID) } returns null
            coEvery { dao.insertEpisode(episode) } just Runs

            repository.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL)
            coVerify {
                api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL)
                dao.getEpisode(EPISODE_ID)
                dao.insertEpisode(episode)
            }
        }

    @Test
    fun filterEpisodes_returns_episodesFromDB_when_networkErrorOccurs() = runBlocking {
        coEvery { api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL) } throws Exception()
        coEvery { dao.filterEpisodes("%$EPISODE_NAME%", "%$EPISODE_SYMBOL%") } returns episodes

        assertEquals(episodeList, repository.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL))
        coVerify {
            api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL)
            dao.filterEpisodes("%$EPISODE_NAME%", "%$EPISODE_SYMBOL%")
        }
    }

    @Test
    fun filterEpisodes_returns_null_when_networkErrorOccurs_and_dBHasNoEpisodesNeeded() =
        runBlocking {
            coEvery { api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL) } throws Exception()
            coEvery { dao.filterEpisodes("%$EPISODE_NAME%", "%$EPISODE_SYMBOL%") } returns null

            assertEquals(null, repository.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL))
            coVerify {
                api.filterEpisodes(EPISODE_NAME, EPISODE_SYMBOL)
                dao.filterEpisodes("%$EPISODE_NAME%", "%$EPISODE_SYMBOL%")
            }
        }

    @Test
    fun getEpisode_returns_episodeFromDB_when_dBHasThisEpisode() = runBlocking {
        coEvery { dao.getEpisode(EPISODE_ID) } returns episode
        assertEquals(episode, repository.getEpisode(EPISODE_ID))
        coVerify {
            dao.getEpisode(EPISODE_ID)
            api wasNot Called
        }
    }

    @Test
    fun getEpisode_returns_episodeFromServer_when_dBHasNoThisEpisode_andSaves_itIntoDB() =
        runBlocking {
            coEvery { dao.getEpisode(EPISODE_ID) } returns null
            coEvery { api.getEpisode(EPISODE_ID) } returns episode
            coEvery { dao.insertEpisode(episode) } just Runs

            assertEquals(episode, repository.getEpisode(EPISODE_ID))
            coVerify {
                dao.getEpisode(EPISODE_ID)
                api.getEpisode(EPISODE_ID)
                dao.insertEpisode(episode)
            }
        }

    @Test
    fun getEpisode_returns_null_when_noEpisodeInDBAndErrorNetworkOccurs() = runBlocking {
        coEvery { dao.getEpisode(EPISODE_ID) } returns null
        coEvery { api.getEpisode(EPISODE_ID) } throws Exception()

        assertEquals(null, repository.getEpisode(EPISODE_ID))
        coVerify {
            dao.getEpisode(EPISODE_ID)
            api.getEpisode(EPISODE_ID)
        }
    }
}