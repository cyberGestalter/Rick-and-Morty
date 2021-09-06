package com.example.rick_and_morty.dagger

import com.example.rick_and_morty.viewmodel.RickAndMortyViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelFactoryApi::class])
interface ViewModelFactoryComponent {
    fun getFactory(): RickAndMortyViewModelFactory
}