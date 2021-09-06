package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Episode
import com.example.data.entities.EpisodeList
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
class EpisodeViewModelTest : BaseViewModelTest() {

    @InjectMockKs
    private lateinit var viewModel: EpisodeViewModel

    private fun assertFilterEquals(name: String, episode: String) {
        assertEquals(name, viewModel.nameFilter)
        assertEquals(episode, viewModel.episodeFilter)
    }

    @Test
    fun dropFiltersAndUpdateEpisodes_turnsAllFiltersIntoEmptyStrings() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterEpisodes(NAME, EPISODE) }
            coExcludeRecords { repository.getEpisodeList() }

            viewModel.filterList(NAME, EPISODE)
            assertFilterEquals(NAME, EPISODE)

            viewModel.dropFiltersAndUpdateEpisodes()
            assertFilterEquals("", "")
        }
    }

    @Test
    fun dropFiltersAndUpdateEpisodes_changesInnerEpisodeList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val episodeList = createEpisodeList(null, listOf(createEpisode(ID, NAME, EPISODE)))
            coEvery { repository.getEpisodeList() } returns episodeList
            viewModel.dropFiltersAndUpdateEpisodes()
            assertEquals(episodeList, viewModel.episodeList.value)
            coVerify { repository.getEpisodeList() }
        }
    }

    @Test
    fun addNextEpisodes_addsNewEpisodesIntoInnerEpisodeList_when_nextPageIsNotNull() {
        dispatcher.runBlockingTest {
            val nextPageNumber = 2
            val nextPage = "https://rickandmortyapi.com/api/episode?page=$nextPageNumber"

            val episode1 = createEpisode(ID, NAME, EPISODE)
            val episodeList1 = createEpisodeList(nextPage, listOf(episode1))
            val episode2 = createEpisode(ID2, NAME2, EPISODE2)
            val episodeList2 = createEpisodeList(null, listOf(episode2))

            val newEpisodeList = createEpisodeList(null, listOf(episode1, episode2))

            coEvery { repository.getEpisodeList() } returns episodeList1
            coEvery {
                repository.getEpisodeList(nextPageNumber, "", "")
            } returns episodeList2

            viewModel.dropFiltersAndUpdateEpisodes()
            viewModel.addNextEpisodes()

            assertEquals(newEpisodeList, viewModel.episodeList.value)
            coVerify {
                repository.getEpisodeList()
                repository.getEpisodeList(nextPageNumber, "", "")
            }
        }
    }

    @Test
    fun addNextEpisodes_doesNotChangeInnerEpisodeList_when_nextPageIsNull() {
        dispatcher.runBlockingTest {
            val nextPage = null
            val episode1 = createEpisode(ID, NAME, EPISODE)
            val episodeList1 = createEpisodeList(nextPage, listOf(episode1))

            coEvery { repository.getEpisodeList() } returns episodeList1

            viewModel.dropFiltersAndUpdateEpisodes()
            viewModel.addNextEpisodes()

            assertEquals(episodeList1, viewModel.episodeList.value)

            coVerify { repository.getEpisodeList() }
        }
    }

    @Test
    fun filterListByName_changesNameFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterEpisodes(NAME, "") }
            coExcludeRecords { repository.getEpisodeList() }

            viewModel.dropFiltersAndUpdateEpisodes()
            assertFilterEquals("", "")
            viewModel.filterListByName(NAME)
            assertFilterEquals(NAME, "")
        }
    }

    @Test
    fun filterListByName_changesInnerEpisodeList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val episode1 = createEpisode(ID, NAME, EPISODE)
            val episode2 = createEpisode(ID2, NAME2, EPISODE2)

            val episodeList1 = createEpisodeList(nextPage, listOf(episode1, episode2))
            val episodeList2 = createEpisodeList(nextPage, listOf(episode2))

            coEvery { repository.getEpisodeList() } returns episodeList1
            coEvery { repository.filterEpisodes(NAME2, "") } returns episodeList2

            viewModel.dropFiltersAndUpdateEpisodes()
            assertEquals(episodeList1, viewModel.episodeList.value)
            viewModel.filterListByName(NAME2)
            assertEquals(episodeList2, viewModel.episodeList.value)

            coVerify {
                repository.getEpisodeList()
                repository.filterEpisodes(NAME2, "")
            }
        }
    }

    @Test
    fun filterList_changesAllFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterEpisodes(NAME, EPISODE) }
            coExcludeRecords { repository.getEpisodeList() }

            viewModel.dropFiltersAndUpdateEpisodes()
            assertFilterEquals("", "")
            viewModel.filterList(NAME, EPISODE)
            assertFilterEquals(NAME, EPISODE)
        }
    }

    @Test
    fun filterList_changesInnerEpisodeList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val episode1 = createEpisode(ID, NAME, EPISODE)
            val episode2 = createEpisode(ID2, NAME2, EPISODE)

            val episodeList1 = createEpisodeList(nextPage, listOf(episode1, episode2))
            val episodeList2 = createEpisodeList(nextPage, listOf(episode2))

            coEvery { repository.getEpisodeList() } returns episodeList1
            coEvery { repository.filterEpisodes(NAME2, EPISODE2) } returns episodeList2

            viewModel.dropFiltersAndUpdateEpisodes()
            assertEquals(episodeList1, viewModel.episodeList.value)
            viewModel.filterList(NAME2, EPISODE2)
            assertEquals(episodeList2, viewModel.episodeList.value)

            coVerify {
                repository.getEpisodeList()
                repository.filterEpisodes(NAME2, EPISODE2)
            }
        }
    }

    @Test
    fun getSeason_returns_seasonNumberStringFromEpisodeSymbolLiteral() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.getEpisodeList() }

            val testData = Array(4) { Array(2) { "" } }
            testData[0] = arrayOf("", "")
            testData[1] = arrayOf("E01", "")
            testData[2] = arrayOf("S01", "1")
            testData[3] = arrayOf("S01E01", "1")

            testData.map { (episodeSymbol, expected) ->
                coExcludeRecords { repository.filterEpisodes("", episodeSymbol) }
                viewModel.filterList("", episodeSymbol)
                assertEquals(expected, viewModel.getSeason())
            }
        }
    }

    @Test
    fun getEpisode_returns_episodeNumberStringFromEpisodeSymbolLiteral() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.getEpisodeList() }

            val testData = Array(4) { Array(2) { "" } }
            testData[0] = arrayOf("", "")
            testData[1] = arrayOf("S01", "")
            testData[2] = arrayOf("E01", "1")
            testData[3] = arrayOf("S01E01", "1")

            testData.map { (episodeSymbol, expected) ->
                coExcludeRecords { repository.filterEpisodes("", episodeSymbol) }
                viewModel.filterList("", episodeSymbol)
                assertEquals(expected, viewModel.getEpisode())
            }
        }
    }

    @Test
    fun getEpisodeSymbol_returns_episodeSymbolLiteralFromSeasonAndEpisodeStringNumbers() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.getEpisodeList() }

            val testData = Array(12) { Array(3) { "" } }
            testData[0] = arrayOf("", "", "")
            testData[1] = arrayOf("0", "", "")
            testData[2] = arrayOf("", "0", "")
            testData[3] = arrayOf("0", "0", "")
            testData[4] = arrayOf("1", "", "S01")
            testData[5] = arrayOf("10", "", "S10")
            testData[6] = arrayOf("", "1", "E01")
            testData[7] = arrayOf("", "10", "E10")
            testData[8] = arrayOf("1", "1", "S01E01")
            testData[9] = arrayOf("1", "10", "S01E10")
            testData[10] = arrayOf("10", "1", "S10E01")
            testData[11] = arrayOf("10", "10", "S10E10")

            testData.map { (seasonNumber, episodeNumber, expected) ->
                assertEquals(expected, viewModel.getEpisodeSymbol(seasonNumber, episodeNumber))
            }
        }
    }

    companion object {
        const val ID = 1
        const val NAME = "Pilot"
        const val EPISODE = "S01E01"

        const val ID2 = 2
        const val NAME2 = "Pilot2"
        const val EPISODE2 = "S02E02"

        private fun createEpisode(id: Int, name: String, episode: String) =
            Episode(id, name, "", episode, listOf(), "", "")

        private fun createEpisodeList(nextPage: String?, episodes: List<Episode>) =
            EpisodeList(Info(0, 0, nextPage, null), episodes)
    }
}