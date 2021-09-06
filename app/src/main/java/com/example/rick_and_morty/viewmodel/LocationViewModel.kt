package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Location
import com.example.data.entities.LocationList
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: IRepository) : BaseListViewModel() {
    private val _locationList = MutableLiveData<LocationList?>()
    val locationList: LiveData<LocationList?>
        get() = _locationList

    private var _nameFilter = ""
    val nameFilter: String
        get() = _nameFilter

    private var _typeFilter = ""
    val typeFilter: String
        get() = _typeFilter

    private var _dimensionFilter = ""
    val dimensionFilter: String
        get() = _dimensionFilter

    private val merger = Merger<Location>()

    init {
        dropFiltersAndUpdateLocations()
    }

    fun dropFiltersAndUpdateLocations() {
        changeFilters("", "", "")
        viewModelScope.launch {
            val locationList = repository.getLocationList()
            _locationList.value = locationList
        }
    }

    fun addNextLocations() {
        viewModelScope.launch {
            getNextPageNumberString(_locationList.value?.info?.next)?.let {
                val locationList =
                    repository.getLocationList(
                        it.toInt(),
                        _nameFilter,
                        _typeFilter,
                        _dimensionFilter
                    )
                locationList?.results =
                    merger.mergeLists(_locationList.value?.results, locationList?.results)
                _locationList.value = locationList
            }
        }
    }

    fun filterListByName(name: String) {
        _nameFilter = name
        filterList()
    }

    fun filterList(name: String, type: String, dimension: String) {
        changeFilters(name, type, dimension)
        filterList()
    }

    private fun filterList() {
        viewModelScope.launch {
            val locationList = repository
                .filterLocations(_nameFilter, _typeFilter, _dimensionFilter)
            _locationList.value = locationList
        }
    }

    private fun changeFilters(name: String, type: String, dimension: String) {
        _nameFilter = name
        _typeFilter = type
        _dimensionFilter = dimension
    }
}