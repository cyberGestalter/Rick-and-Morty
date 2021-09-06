package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Character
import com.example.data.entities.Location
import kotlinx.coroutines.launch

class LocationDetailsViewModel(private val repository: IRepository) : BaseDetailsViewModel() {
    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?>
        get() = _location

    private var _characters = MutableLiveData<MutableList<Character>>()
    val characters: LiveData<MutableList<Character>>
        get() = _characters

    fun updateLocation(id: Int) {
        viewModelScope.launch {
            val location = repository.getLocation(id)
            location?.let {
                val locationCharacters = mutableListOf<Character>()
                it.residents.forEach { characterLink ->
                    repository.getCharacter(getIdFromLink(characterLink))?.let { character ->
                        locationCharacters.add(character)
                    }
                }
                _characters.value = locationCharacters
            }
            _location.value = location
            _created.value = location?.created
        }
    }

    fun dropLocation() {
        _location.value = null
        _created.value = null
        _characters.value = mutableListOf()
    }
}