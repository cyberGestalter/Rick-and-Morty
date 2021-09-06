package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(private val repository: IRepository) : BaseDetailsViewModel() {
    private val _character = MutableLiveData<Character?>()
    val character: LiveData<Character?>
        get() = _character

    private var _origin = MutableLiveData<Location?>()
    val origin: LiveData<Location?>
        get() = _origin

    private var _location = MutableLiveData<Location?>()
    val location: LiveData<Location?>
        get() = _location

    private var _episodes = MutableLiveData<MutableList<Episode>>()
    val episodes: LiveData<MutableList<Episode>>
        get() = _episodes

    fun updateCharacter(id: Int) {
        viewModelScope.launch {
            val character = repository.getCharacter(id)
            character?.let {
                _origin.value = repository.getLocation(getIdFromLink(it.origin.url))
                _location.value = repository.getLocation(getIdFromLink(it.location.url))
                val characterEpisodes = mutableListOf<Episode>()
                it.episode.forEach { episodeLink ->
                    repository.getEpisode(getIdFromLink(episodeLink))?.let { episode ->
                        characterEpisodes.add(episode)
                    }
                }
                _episodes.value = characterEpisodes
            }
            _character.value = character
            _created.value = character?.created
        }
    }

    fun dropCharacter() {
        _character.value = null
        _created.value = null
        _origin.value = null
        _location.value = null
        _episodes.value = mutableListOf()
    }
}