package com.example.rick_and_morty.dagger

import android.content.Context
import androidx.room.Room
import com.example.data.database.RickyAndMortyDao
import com.example.data.database.RickyAndMortyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ContextModule::class])
class RoomApi {

    @Singleton
    @Provides
    fun rickyAndMortyDatabase(context: Context): RickyAndMortyDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            RickyAndMortyDatabase::class.java,
            DB_NAME
        )
            .build()

    @Singleton
    @Provides
    fun rickyAndMortyDao(rickyAndMortyDatabase: RickyAndMortyDatabase): RickyAndMortyDao =
        rickyAndMortyDatabase.getDao()

    companion object {
        const val DB_NAME = "ricky_and_morty_db"
    }
}