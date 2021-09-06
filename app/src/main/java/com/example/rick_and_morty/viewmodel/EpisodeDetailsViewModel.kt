package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Character
import com.example.data.entities.Episode
import kotlinx.coroutines.launch

class EpisodeDetailsViewModel(private val repository: IRepository) : BaseDetailsViewModel() {
    private val _episode = MutableLiveData<Episode?>()
    val episode: LiveData<Episode?>
        get() = _episode

    private var _characters = MutableLiveData<MutableList<Character>>()
    val characters: LiveData<MutableList<Character>>
        get() = _characters

    fun updateEpisode(id: Int) {
        viewModelScope.launch {
            val episode = repository.getEpisode(id)
            episode?.let {
                val episodeCharacters = mutableListOf<Character>()
                it.characters.forEach { characterLink ->
                    repository.getCharacter(getIdFromLink(characterLink))?.let { episode ->
                        episodeCharacters.add(episode)
                    }
                }
                _characters.value = episodeCharacters
            }
            _episode.value = episode
            _created.value = episode?.created
        }
    }

    fun dropEpisode() {
        _episode.value = null
        _created.value = null
        _characters.value = mutableListOf()
    }
}