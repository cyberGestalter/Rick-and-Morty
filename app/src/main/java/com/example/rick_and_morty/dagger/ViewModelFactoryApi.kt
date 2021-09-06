package com.example.rick_and_morty.dagger

import com.example.data.IRepository
import com.example.data.database.RickyAndMortyDao
import com.example.data.network.RickAndMortyAPIService
import com.example.data.repository.Repository
import com.example.rick_and_morty.viewmodel.RickAndMortyViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RetrofitApi::class, RoomApi::class])
class ViewModelFactoryApi {
    @Singleton
    @Provides
    fun rickAndMortyViewModelFactory(repository: IRepository): RickAndMortyViewModelFactory =
        RickAndMortyViewModelFactory.getFactory(repository)

    @Singleton
    @Provides
    fun repository(
        rickAndMortyAPIService: RickAndMortyAPIService,
        rickyAndMortyDao: RickyAndMortyDao
    ): IRepository = Repository(rickAndMortyAPIService, rickyAndMortyDao)
}