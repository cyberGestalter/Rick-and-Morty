package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Character
import com.example.data.entities.CharacterList
import com.example.data.entities.CharacterLocation
import com.example.data.entities.Info
import io.mockk.coEvery
import io.mockk.coExcludeRecords
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterViewModelTest : BaseViewModelTest() {

    @InjectMockKs
    private lateinit var viewModel: CharacterViewModel

    private fun assertFilterEquals(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ) {
        assertEquals(name, viewModel.nameFilter)
        assertEquals(status, viewModel.statusFilter)
        assertEquals(species, viewModel.speciesFilter)
        assertEquals(type, viewModel.typeFilter)
        assertEquals(gender, viewModel.genderFilter)
    }

    @Test
    fun dropFiltersAndUpdateCharacters_turnsAllFiltersIntoEmptyStrings() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterCharacters(NAME, STATUS, SPECIES, TYPE, GENDER) }
            coExcludeRecords { repository.getCharacterList() }

            viewModel.filterList(NAME, STATUS, SPECIES, TYPE, GENDER)
            assertFilterEquals(NAME, STATUS, SPECIES, TYPE, GENDER)

            viewModel.dropFiltersAndUpdateCharacters()
            assertFilterEquals("", "", "", "", "")
        }
    }

    @Test
    fun dropFiltersAndUpdateCharacters_changesInnerCharacterList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val characterList = createCharacterList(
                null,
                listOf(createCharacter(ID, NAME, STATUS, SPECIES, TYPE, GENDER))
            )
            coEvery { repository.getCharacterList() } returns characterList
            viewModel.dropFiltersAndUpdateCharacters()
            assertEquals(characterList, viewModel.characterList.value)
            coVerify { repository.getCharacterList() }
        }
    }

    @Test
    fun addNextCharacters_addsNewCharactersIntoInnerCharacterList_when_nextPageIsNotNull() {
        dispatcher.runBlockingTest {
            val nextPageNumber = 2
            val nextPage = "https://rickandmortyapi.com/api/character?page=$nextPageNumber"

            val character1 = createCharacter(ID, NAME, STATUS, SPECIES, TYPE, GENDER)
            val characterList1 = createCharacterList(nextPage, listOf(character1))
            val character2 = createCharacter(ID2, NAME2, STATUS2, SPECIES2, TYPE2, GENDER2)
            val characterList2 = createCharacterList(null, listOf(character2))

            val newCharacterList = createCharacterList(null, listOf(character1, character2))

            coEvery { repository.getCharacterList() } returns characterList1
            coEvery {
                repository.getCharacterList(
                    nextPageNumber, "", "", "", "", ""
                )
            } returns characterList2

            viewModel.dropFiltersAndUpdateCharacters()
            viewModel.addNextCharacters()

            assertEquals(newCharacterList, viewModel.characterList.value)
            coVerify {
                repository.getCharacterList()
                repository.getCharacterList(nextPageNumber, "", "", "", "", "")
            }
        }
    }

    @Test
    fun addNextCharacters_doesNotChangeInnerCharacterList_when_nextPageIsNull() {
        dispatcher.runBlockingTest {
            val nextPage = null
            val character1 = createCharacter(ID, NAME, STATUS, SPECIES, TYPE, GENDER)
            val characterList1 = createCharacterList(nextPage, listOf(character1))

            coEvery { repository.getCharacterList() } returns characterList1

            viewModel.dropFiltersAndUpdateCharacters()
            viewModel.addNextCharacters()

            assertEquals(characterList1, viewModel.characterList.value)

            coVerify { repository.getCharacterList() }
        }
    }

    @Test
    fun filterListByName_changesNameFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterCharacters(NAME, "", "", "", "") }
            coExcludeRecords { repository.getCharacterList() }

            viewModel.dropFiltersAndUpdateCharacters()
            assertFilterEquals("", "", "", "", "")
            viewModel.filterListByName(NAME)
            assertFilterEquals(NAME, "", "", "", "")
        }
    }

    @Test
    fun filterListByName_changesInnerCharacterList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val character1 = createCharacter(ID, NAME, STATUS, SPECIES, TYPE, GENDER)
            val character2 = createCharacter(ID2, NAME2, STATUS2, SPECIES2, TYPE2, GENDER2)

            val characterList1 = createCharacterList(nextPage, listOf(character1, character2))
            val characterList2 = createCharacterList(nextPage, listOf(character2))

            coEvery { repository.getCharacterList() } returns characterList1
            coEvery { repository.filterCharacters(NAME2, "", "", "", "") } returns characterList2

            viewModel.dropFiltersAndUpdateCharacters()
            assertEquals(characterList1, viewModel.characterList.value)
            viewModel.filterListByName(NAME2)
            assertEquals(characterList2, viewModel.characterList.value)

            coVerify {
                repository.getCharacterList()
                repository.filterCharacters(NAME2, "", "", "", "")
            }
        }
    }

    @Test
    fun filterList_changesAllFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterCharacters(NAME, STATUS, SPECIES, TYPE, GENDER) }
            coExcludeRecords { repository.getCharacterList() }

            viewModel.dropFiltersAndUpdateCharacters()
            assertFilterEquals("", "", "", "", "")
            viewModel.filterList(NAME, STATUS, SPECIES, TYPE, GENDER)
            assertFilterEquals(NAME, STATUS, SPECIES, TYPE, GENDER)
        }
    }

    @Test
    fun filterList_changesInnerCharacterList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val character1 = createCharacter(ID, NAME, STATUS, SPECIES, TYPE, GENDER)
            val character2 = createCharacter(ID2, NAME2, STATUS2, SPECIES2, TYPE2, GENDER2)

            val characterList1 = createCharacterList(nextPage, listOf(character1, character2))
            val characterList2 = createCharacterList(nextPage, listOf(character2))

            coEvery { repository.getCharacterList() } returns characterList1
            coEvery {
                repository.filterCharacters(
                    NAME2,
                    STATUS2,
                    SPECIES2,
                    TYPE2,
                    GENDER2
                )
            } returns characterList2

            viewModel.dropFiltersAndUpdateCharacters()
            assertEquals(characterList1, viewModel.characterList.value)
            viewModel.filterList(NAME2, STATUS2, SPECIES2, TYPE2, GENDER2)
            assertEquals(characterList2, viewModel.characterList.value)

            coVerify {
                repository.getCharacterList()
                repository.filterCharacters(NAME2, STATUS2, SPECIES2, TYPE2, GENDER2)
            }
        }
    }

    companion object {
        const val ID = 1
        const val NAME = "Morty"
        const val STATUS = "Alive"
        const val SPECIES = "Human"
        const val TYPE = ""
        const val GENDER = "Male"

        const val ID2 = 2
        const val NAME2 = "Morty2"
        const val STATUS2 = "Alive2"
        const val SPECIES2 = "Human2"
        const val TYPE2 = "2"
        const val GENDER2 = "Male2"

        private fun createCharacter(
            id: Int, name: String, status: String, species: String, type: String, gender: String
        ) = Character(
            id, name, status, species, type, gender,
            CharacterLocation(), CharacterLocation(), "", listOf(), "", ""
        )

        private fun createCharacterList(nextPage: String?, characters: List<Character>) =
            CharacterList(Info(0, 0, nextPage, null), characters)
    }
}