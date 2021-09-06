package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Character
import com.example.data.entities.CharacterList
import kotlinx.coroutines.launch

class CharacterViewModel(private val repository: IRepository) : BaseListViewModel() {

    private val _characterList = MutableLiveData<CharacterList?>()
    val characterList: LiveData<CharacterList?>
        get() = _characterList

    private var _nameFilter = ""
    val nameFilter: String
        get() = _nameFilter

    private var _statusFilter = ""
    val statusFilter: String
        get() = _statusFilter

    private var _speciesFilter = ""
    val speciesFilter: String
        get() = _speciesFilter

    private var _typeFilter = ""
    val typeFilter: String
        get() = _typeFilter

    private var _genderFilter = ""
    val genderFilter: String
        get() = _genderFilter

    private val merger = Merger<Character>()

    init {
        dropFiltersAndUpdateCharacters()
    }

    fun dropFiltersAndUpdateCharacters() {
        changeFilters("", "", "", "", "")
        viewModelScope.launch {
            val characterList = repository.getCharacterList()
            _characterList.value = characterList
        }
    }

    fun addNextCharacters() {
        viewModelScope.launch {
            getNextPageNumberString(_characterList.value?.info?.next)?.let {
                val characterList = repository.getCharacterList(
                    it.toInt(),
                    _nameFilter,
                    _statusFilter,
                    _speciesFilter,
                    _typeFilter,
                    _genderFilter
                )
                characterList?.results =
                    merger.mergeLists(_characterList.value?.results, characterList?.results)
                _characterList.value = characterList
            }
        }
    }

    fun filterListByName(name: String) {
        _nameFilter = name
        filterList()
    }

    fun filterList(name: String, status: String, species: String, type: String, gender: String) {
        changeFilters(name, status, species, type, gender)
        filterList()
    }

    private fun filterList() {
        viewModelScope.launch {
            val characterList = repository.filterCharacters(
                _nameFilter, _statusFilter, _speciesFilter, _typeFilter, _genderFilter
            )
            _characterList.value = characterList
        }
    }

    private fun changeFilters(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ) {
        _nameFilter = name
        _statusFilter = status
        _speciesFilter = species
        _typeFilter = type
        _genderFilter = gender
    }
}