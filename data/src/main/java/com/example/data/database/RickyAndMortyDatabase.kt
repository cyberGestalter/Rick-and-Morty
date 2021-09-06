package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.ListConverter
import com.example.data.entities.Location

@TypeConverters(ListConverter::class)
@Database(
    entities = [Character::class, Location::class, Episode::class],
    version = 1,
    exportSchema = false
)
abstract class RickyAndMortyDatabase : RoomDatabase() {
    abstract fun getDao(): RickyAndMortyDao
}