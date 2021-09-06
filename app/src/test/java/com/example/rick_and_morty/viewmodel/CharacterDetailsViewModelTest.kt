package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Character
import com.example.data.entities.CharacterLocation
import com.example.data.entities.Episode
import com.example.data.entities.Location
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterDetailsViewModelTest : BaseViewModelTest() {

    @InjectMockKs
    private lateinit var viewModel: CharacterDetailsViewModel

    private fun assertInnerFields(
        character: Character?,
        origin: Location?,
        location: Location?,
        episodes: MutableList<Episode>?,
        created: String?
    ) {
        assertEquals(character, viewModel.character.value)
        assertEquals(origin, viewModel.origin.value)
        assertEquals(location, viewModel.location.value)
        assertEquals(episodes, viewModel.episodes.value)
        assertEquals(created, viewModel.created.value)
    }

    @Test
    fun updateCharacter_changesAllInnerVariables_when_repositoryReturnsCharacter() {
        dispatcher.runBlockingTest {
            val character = createCharacter()
            val location = createLocation()
            val episode = createEpisode()
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character
            coEvery { repository.getLocation(LOCATION_ID) } returns location
            coEvery { repository.getEpisode(EPISODE_ID) } returns episode

            assertInnerFields(null, null, null, null, null)

            viewModel.updateCharacter(CHARACTER_ID)

            assertInnerFields(character, location, location, mutableListOf(episode), CREATED_PARSED)

            coVerify {
                repository.getCharacter(CHARACTER_ID)
                repository.getLocation(LOCATION_ID)
                repository.getEpisode(EPISODE_ID)
            }
        }
    }

    @Test
    fun updateCharacter_doesNotChangeInnerVariables_and_turnsCreatedIntoEmptyString_when_repositoryReturnsNull() {
        dispatcher.runBlockingTest {
            coEvery { repository.getCharacter(CHARACTER_ID) } returns null

            assertInnerFields(null, null, null, null, null)
            viewModel.updateCharacter(CHARACTER_ID)
            assertInnerFields(null, null, null, null, "")

            coVerify {
                repository.getCharacter(CHARACTER_ID)
            }
        }
    }

    @Test
    fun dropCharacter_turnsAllInnerVariablesIntoNull_and_turnsEpisodesIntoEmptyList_and_turnsCreatedIntoEmptyString() {
        dispatcher.runBlockingTest {
            val character = createCharacter()
            val location = createLocation()
            val episode = createEpisode()
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character
            coEvery { repository.getLocation(LOCATION_ID) } returns location
            coEvery { repository.getEpisode(EPISODE_ID) } returns episode

            viewModel.updateCharacter(CHARACTER_ID)
            assertInnerFields(character, location, location, mutableListOf(episode), CREATED_PARSED)

            viewModel.dropCharacter()
            assertInnerFields(null, null, null, mutableListOf(), "")

            coVerify {
                repository.getCharacter(CHARACTER_ID)
                repository.getLocation(LOCATION_ID)
                repository.getEpisode(EPISODE_ID)
            }
        }
    }

    companion object {
        const val CHARACTER_ID = 1
        const val LOCATION_ID = 1
        const val EPISODE_ID = 1

        private const val CREATED = "2017-11-04T18:48:46.250Z"
        const val CREATED_PARSED = "November 4, 2017"

        private fun createCharacter() = Character(
            CHARACTER_ID, "Morty", "Alive", "Human", "", "Male",
            CharacterLocation("", "https://rickandmortyapi.com/api/location/$LOCATION_ID"),
            CharacterLocation("", "https://rickandmortyapi.com/api/location/$LOCATION_ID"),
            "",
            listOf("https://rickandmortyapi.com/api/episode/$EPISODE_ID"),
            "",
            CREATED
        )

        private fun createLocation() =
            Location(LOCATION_ID, "Gaia", "Planet", "Dimension", listOf(), "", "")

        private fun createEpisode() =
            Episode(EPISODE_ID, "Pilot", "", "S01E01", listOf(), "", "")
    }
}