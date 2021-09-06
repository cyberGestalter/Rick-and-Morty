package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.IRepository

class RickAndMortyViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(CharacterDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterDetailsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(LocationDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationDetailsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodeViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(EpisodeDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EpisodeDetailsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        private lateinit var repository: IRepository
        fun getFactory(newRepository: IRepository) =
            RickAndMortyViewModelFactory().also { repository = newRepository }
    }
}