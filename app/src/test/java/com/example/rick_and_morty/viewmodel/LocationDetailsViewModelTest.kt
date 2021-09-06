package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Character
import com.example.data.entities.CharacterLocation
import com.example.data.entities.Location
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationDetailsViewModelTest : BaseViewModelTest() {
    @InjectMockKs
    private lateinit var viewModel: LocationDetailsViewModel

    private fun assertInnerFields(
        location: Location?,
        characters: MutableList<Character>?,
        created: String?
    ) {
        assertEquals(location, viewModel.location.value)
        assertEquals(characters, viewModel.characters.value)
        assertEquals(created, viewModel.created.value)
    }

    @Test
    fun updateLocation_changesAllInnerVariables_when_repositoryReturnsLocation() {
        dispatcher.runBlockingTest {
            val location = createLocation()
            val character = createCharacter()
            coEvery { repository.getLocation(LOCATION_ID) } returns location
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character

            assertInnerFields(null, null, null)

            viewModel.updateLocation(LOCATION_ID)

            assertInnerFields(location, mutableListOf(character), CREATED_PARSED)

            coVerify {
                repository.getLocation(LOCATION_ID)
                repository.getCharacter(CHARACTER_ID)
            }
        }
    }

    @Test
    fun updateLocation_doesNotChangeInnerVariables_and_turnsCreatedIntoEmptyString_when_repositoryReturnsNull() {
        dispatcher.runBlockingTest {
            coEvery { repository.getLocation(LOCATION_ID) } returns null

            assertInnerFields(null, null, null)
            viewModel.updateLocation(LOCATION_ID)
            assertInnerFields(null, null, "")

            coVerify {
                repository.getLocation(LOCATION_ID)
            }
        }
    }

    @Test
    fun dropLocation_turnsAllInnerVariablesIntoNull_and_turnsCharactersIntoEmptyList_and_turnsCreatedIntoEmptyString() {
        dispatcher.runBlockingTest {
            val location = createLocation()
            val character = createCharacter()
            coEvery { repository.getLocation(LOCATION_ID) } returns location
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character

            viewModel.updateLocation(LOCATION_ID)
            assertInnerFields(location, mutableListOf(character), CREATED_PARSED)

            viewModel.dropLocation()
            assertInnerFields(null, mutableListOf(), "")

            coVerify {
                repository.getLocation(LOCATION_ID)
                repository.getCharacter(CHARACTER_ID)
            }
        }
    }

    companion object {
        const val LOCATION_ID = 1
        const val CHARACTER_ID = 1

        private const val CREATED = "2017-11-04T18:48:46.250Z"
        const val CREATED_PARSED = "November 4, 2017"

        private fun createLocation() =
            Location(
                LOCATION_ID, "Gaia", "Planet", "Dimension",
                listOf("https://rickandmortyapi.com/api/character/$CHARACTER_ID"), "", CREATED
            )

        private fun createCharacter() = Character(
            CHARACTER_ID, "Morty", "Alive", "Human", "", "Male",
            CharacterLocation(), CharacterLocation(), "", listOf(), "", ""
        )
    }
}