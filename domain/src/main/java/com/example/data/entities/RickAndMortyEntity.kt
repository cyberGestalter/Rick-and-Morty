package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

sealed class RickAndMortyEntity

@Entity(tableName = "characters")
@TypeConverters(CharacterLocationConverter::class)
data class Character(
    @PrimaryKey var id: Int,
    var name: String,
    var status: String,
    var species: String,
    var type: String,
    var gender: String,
    var origin: CharacterLocation,
    var location: CharacterLocation,
    var image: String,
    var episode: List<String>,
    var url: String,
    var created: String
) : RickAndMortyEntity() {
    constructor() : this(
        0, "", "", "", "", "", CharacterLocation(),
        CharacterLocation(), "", listOf(), "", ""
    )
}

data class CharacterLocation(var name: String, var url: String) {
    constructor() : this("", "")
}

data class CharacterList(val info: Info, var results: List<Character>)

class CharacterLocationConverter {
    @TypeConverter
    fun fromCharacterLocationToUrl(characterLocation: CharacterLocation): String {
        return characterLocation.url
    }

    @TypeConverter
    fun fromUrlToCharacterLocation(url: String): CharacterLocation {
        return CharacterLocation("", url)
    }
}

class ListConverter {
    private val jsonAdapter: JsonAdapter<List<String>> =
        Moshi.Builder()
            .build()
            .adapter(Types.newParameterizedType(List::class.java, String::class.java))

    @TypeConverter
    fun fromListToJson(list: List<String>): String {
        return jsonAdapter.toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json: String): List<String>? {
        return jsonAdapter.fromJson(json)
    }
}

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey var id: Int,
    var name: String,
    var type: String,
    var dimension: String,
    var residents: List<String>,
    var url: String,
    var created: String
) : RickAndMortyEntity() {
    constructor() : this(0, "", "", "", listOf(), "", "")
}

data class LocationList(val info: Info, var results: List<Location>)

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey var id: Int,
    var name: String,
    @Json(name = "air_date")
    var airDate: String,
    var episode: String,
    var characters: List<String>,
    var url: String,
    var created: String
) : RickAndMortyEntity() {
    constructor() : this(0, "", "", "", listOf(), "", "")
}

data class EpisodeList(val info: Info, var results: List<Episode>)

data class Info(val count: Int, val pages: Int, val next: String?, val prev: String?)