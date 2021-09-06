package com.example.rick_and_morty.viewmodel

import com.example.data.entities.Info
import com.example.data.entities.Location
import com.example.data.entities.LocationList
import io.mockk.coEvery
import io.mockk.coExcludeRecords
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationViewModelTest : BaseViewModelTest() {

    @InjectMockKs
    private lateinit var viewModel: LocationViewModel

    private fun assertFilterEquals(name: String, type: String, dimension: String) {
        assertEquals(name, viewModel.nameFilter)
        assertEquals(type, viewModel.typeFilter)
        assertEquals(dimension, viewModel.dimensionFilter)
    }

    @Test
    fun dropFiltersAndUpdateLocations_turnsAllFiltersIntoEmptyStrings() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterLocations(NAME, TYPE, DIMENSION) }
            coExcludeRecords { repository.getLocationList() }

            viewModel.filterList(NAME, TYPE, DIMENSION)
            assertFilterEquals(NAME, TYPE, DIMENSION)

            viewModel.dropFiltersAndUpdateLocations()
            assertFilterEquals("", "", "")
        }
    }

    @Test
    fun dropFiltersAndUpdateLocations_changesInnerLocationList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val locationList =
                createLocationList(null, listOf(createLocation(ID, NAME, TYPE, DIMENSION)))
            coEvery { repository.getLocationList() } returns locationList
            viewModel.dropFiltersAndUpdateLocations()
            assertEquals(locationList, viewModel.locationList.value)
            coVerify { repository.getLocationList() }
        }
    }

    @Test
    fun addNextLocations_addsNewLocationsIntoInnerLocationList_when_nextPageIsNotNull() {
        dispatcher.runBlockingTest {
            val nextPageNumber = 2
            val nextPage = "https://rickandmortyapi.com/api/location?page=$nextPageNumber"

            val location1 = createLocation(ID, NAME, TYPE, DIMENSION)
            val locationList1 = createLocationList(nextPage, listOf(location1))
            val location2 = createLocation(ID2, NAME2, TYPE2, DIMENSION2)
            val locationList2 = createLocationList(null, listOf(location2))

            val newLocationList = createLocationList(null, listOf(location1, location2))

            coEvery { repository.getLocationList() } returns locationList1
            coEvery {
                repository.getLocationList(nextPageNumber, "", "", "")
            } returns locationList2

            viewModel.dropFiltersAndUpdateLocations()
            viewModel.addNextLocations()

            assertEquals(newLocationList, viewModel.locationList.value)
            coVerify {
                repository.getLocationList()
                repository.getLocationList(nextPageNumber, "", "", "")
            }
        }
    }

    @Test
    fun addNextLocations_doesNotChangeInnerLocationList_when_nextPageIsNull() {
        dispatcher.runBlockingTest {
            val nextPage = null
            val location1 = createLocation(ID, NAME, TYPE, DIMENSION)
            val locationList1 = createLocationList(nextPage, listOf(location1))

            coEvery { repository.getLocationList() } returns locationList1

            viewModel.dropFiltersAndUpdateLocations()
            viewModel.addNextLocations()

            assertEquals(locationList1, viewModel.locationList.value)

            coVerify { repository.getLocationList() }
        }
    }

    @Test
    fun filterListByName_changesNameFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterLocations(NAME, "", "") }
            coExcludeRecords { repository.getLocationList() }

            viewModel.dropFiltersAndUpdateLocations()
            assertFilterEquals("", "", "")
            viewModel.filterListByName(NAME)
            assertFilterEquals(NAME, "", "")
        }
    }

    @Test
    fun filterListByName_changesInnerLocationList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val location1 = createLocation(ID, NAME, TYPE, DIMENSION)
            val location2 = createLocation(ID2, NAME2, TYPE2, DIMENSION2)

            val locationList1 = createLocationList(nextPage, listOf(location1, location2))
            val locationList2 = createLocationList(nextPage, listOf(location2))

            coEvery { repository.getLocationList() } returns locationList1
            coEvery { repository.filterLocations(NAME2, "", "") } returns locationList2

            viewModel.dropFiltersAndUpdateLocations()
            assertEquals(locationList1, viewModel.locationList.value)
            viewModel.filterListByName(NAME2)
            assertEquals(locationList2, viewModel.locationList.value)

            coVerify {
                repository.getLocationList()
                repository.filterLocations(NAME2, "", "")
            }
        }
    }

    @Test
    fun filterList_changesAllFilter() {
        dispatcher.runBlockingTest {
            coExcludeRecords { repository.filterLocations(NAME, TYPE, DIMENSION) }
            coExcludeRecords { repository.getLocationList() }

            viewModel.dropFiltersAndUpdateLocations()
            assertFilterEquals("", "", "")
            viewModel.filterList(NAME, TYPE, DIMENSION)
            assertFilterEquals(NAME, TYPE, DIMENSION)
        }
    }

    @Test
    fun filterList_changesInnerLocationList_when_repositoryReturnsValue() {
        dispatcher.runBlockingTest {
            val nextPage: String? = null
            val location1 = createLocation(ID, NAME, TYPE, DIMENSION)
            val location2 = createLocation(ID2, NAME2, TYPE2, DIMENSION2)

            val locationList1 = createLocationList(nextPage, listOf(location1, location2))
            val locationList2 = createLocationList(nextPage, listOf(location2))

            coEvery { repository.getLocationList() } returns locationList1
            coEvery { repository.filterLocations(NAME2, TYPE2, DIMENSION2) } returns locationList2

            viewModel.dropFiltersAndUpdateLocations()
            assertEquals(locationList1, viewModel.locationList.value)
            viewModel.filterList(NAME2, TYPE2, DIMENSION2)
            assertEquals(locationList2, viewModel.locationList.value)

            coVerify {
                repository.getLocationList()
                repository.filterLocations(NAME2, TYPE2, DIMENSION2)
            }
        }
    }

    companion object {
        const val ID = 1
        const val NAME = "Gaia"
        const val TYPE = "Planet"
        const val DIMENSION = "Dimension"

        const val ID2 = 2
        const val NAME2 = "Gaia2"
        const val TYPE2 = "Planet2"
        const val DIMENSION2 = "Dimension2"

        private fun createLocation(id: Int, name: String, type: String, dimension: String) =
            Location(id, name, type, dimension, listOf(), "", "")

        private fun createLocationList(nextPage: String?, locations: List<Location>) =
            LocationList(Info(0, 0, nextPage, null), locations)
    }
}