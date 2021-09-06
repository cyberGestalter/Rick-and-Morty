package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Character
import com.example.data.entities.CharacterLocation
import com.example.data.entities.Episode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class EpisodeDetailsViewModelTest : BaseViewModelTest() {
    @InjectMockKs
    private lateinit var viewModel: EpisodeDetailsViewModel

    private fun assertInnerFields(
        episode: Episode?,
        characters: MutableList<Character>?,
        created: String?
    ) {
        assertEquals(episode, viewModel.episode.value)
        assertEquals(characters, viewModel.characters.value)
        assertEquals(created, viewModel.created.value)
    }

    @Test
    fun updateEpisode_changesAllInnerVariables_when_repositoryReturnsEpisode() {
        dispatcher.runBlockingTest {
            val episode = createEpisode()
            val character = createCharacter()
            coEvery { repository.getEpisode(EPISODE_ID) } returns episode
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character

            assertInnerFields(null, null, null)

            viewModel.updateEpisode(EPISODE_ID)

            assertInnerFields(episode, mutableListOf(character), CREATED_PARSED)

            coVerify {
                repository.getEpisode(EPISODE_ID)
                repository.getCharacter(CHARACTER_ID)
            }
        }
    }

    @Test
    fun updateEpisode_doesNotChangeInnerVariables_and_turnsCreatedIntoEmptyString_when_repositoryReturnsNull() {
        dispatcher.runBlockingTest {
            coEvery { repository.getEpisode(EPISODE_ID) } returns null

            assertInnerFields(null, null, null)
            viewModel.updateEpisode(EPISODE_ID)
            assertInnerFields(null, null, "")

            coVerify {
                repository.getEpisode(EPISODE_ID)
            }
        }
    }

    @Test
    fun dropEpisode_turnsAllInnerVariablesIntoNull_and_turnsCharactersIntoEmptyList_and_turnsCreatedIntoEmptyString() {
        dispatcher.runBlockingTest {
            val episode = createEpisode()
            val character = createCharacter()
            coEvery { repository.getEpisode(EPISODE_ID) } returns episode
            coEvery { repository.getCharacter(CHARACTER_ID) } returns character

            viewModel.updateEpisode(EPISODE_ID)
            assertInnerFields(episode, mutableListOf(character), CREATED_PARSED)

            viewModel.dropEpisode()
            assertInnerFields(null, mutableListOf(), "")

            coVerify {
                repository.getEpisode(EPISODE_ID)
                repository.getCharacter(CHARACTER_ID)
            }
        }
    }

    companion object {
        const val EPISODE_ID = 1
        const val CHARACTER_ID = 1

        private const val CREATED = "2017-11-04T18:48:46.250Z"
        const val CREATED_PARSED = "November 4, 2017"

        private fun createEpisode() =
            Episode(
                EPISODE_ID,
                "Pilot",
                "",
                "S01E01",
                listOf("https://rickandmortyapi.com/api/character/$CHARACTER_ID"),
                "",
                CREATED
            )

        private fun createCharacter() = Character(
            CHARACTER_ID, "Morty", "Alive", "Human", "", "Male",
            CharacterLocation(), CharacterLocation(), "", listOf(), "", ""
        )
    }
}