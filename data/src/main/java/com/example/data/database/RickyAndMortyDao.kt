package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location

@Dao
interface RickyAndMortyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character)

    @Query("SELECT * from characters WHERE id = :id")
    suspend fun getCharacter(id: Int): Character?

    @Query(
        "SELECT * from characters WHERE ((name LIKE :name) AND (status LIKE :status) " +
                "AND (species LIKE :species) AND (type LIKE :type) AND (gender LIKE :gender))"
    )
    suspend fun filterCharacters(
        name: String,
        status: String,
        species: String,
        type: String,
        gender: String
    ): List<Character>?

    @Query("SELECT * from characters")
    suspend fun getCharacters(): List<Character>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: Location)

    @Query("SELECT * from locations WHERE id = :id")
    suspend fun getLocation(id: Int): Location?

    @Query(
        "SELECT * from locations WHERE ((name LIKE :name) AND (type LIKE :type) AND " +
                "(dimension LIKE :dimension))"
    )
    suspend fun filterLocations(
        name: String,
        type: String,
        dimension: String
    ): List<Location>?

    @Query("SELECT * from locations")
    suspend fun getLocations(): List<Location>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode)

    @Query("SELECT * from episodes WHERE id = :id")
    suspend fun getEpisode(id: Int): Episode?

    @Query("SELECT * from episodes WHERE ((name LIKE :name) AND (episode LIKE :episode))")
    suspend fun filterEpisodes(name: String, episode: String): List<Episode>?

    @Query("SELECT * from episodes")
    suspend fun getEpisodes(): List<Episode>?
}