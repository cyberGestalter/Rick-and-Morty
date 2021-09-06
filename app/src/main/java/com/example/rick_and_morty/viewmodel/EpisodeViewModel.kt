package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.IRepository
import com.example.data.entities.Episode
import com.example.data.entities.EpisodeList
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import kotlinx.coroutines.launch

class EpisodeViewModel(private val repository: IRepository) : BaseListViewModel() {
    private val _episodeList = MutableLiveData<EpisodeList?>()
    val episodeList: LiveData<EpisodeList?>
        get() = _episodeList

    private var _nameFilter = ""
    val nameFilter: String
        get() = _nameFilter

    private var _episodeFilter = ""
    val episodeFilter: String
        get() = _episodeFilter

    private val merger = Merger<Episode>()

    init {
        dropFiltersAndUpdateEpisodes()
    }

    fun dropFiltersAndUpdateEpisodes() {
        changeFilters("", "")
        viewModelScope.launch {
            val episodeList = repository.getEpisodeList()
            _episodeList.value = episodeList
        }
    }

    fun addNextEpisodes() {
        viewModelScope.launch {
            getNextPageNumberString(_episodeList.value?.info?.next)?.let {
                val episodeList = repository.getEpisodeList(it.toInt(), _nameFilter, _episodeFilter)
                episodeList?.results =
                    merger.mergeLists(_episodeList.value?.results, episodeList?.results)
                _episodeList.value = episodeList
            }
        }
    }

    fun filterListByName(name: String) {
        _nameFilter = name
        filterList()
    }

    fun filterList(name: String, episode: String) {
        changeFilters(name, episode)
        filterList()
    }

    private fun filterList() {
        viewModelScope.launch {
            val episodeList = repository.filterEpisodes(_nameFilter, _episodeFilter)
            _episodeList.value = episodeList
        }
    }

    private fun changeFilters(name: String, episode: String) {
        _nameFilter = name
        _episodeFilter = episode
    }

    fun getSeason(): String = EpisodeUtil.getSeason(_episodeFilter)
    fun getEpisode(): String = EpisodeUtil.getEpisode(_episodeFilter)
    fun getEpisodeSymbol(seasonNumber: String, episodeNumber: String) =
        EpisodeUtil.getEpisodeSymbol(seasonNumber, episodeNumber)
}

object EpisodeUtil {
    // This method is for to transform S0*E0* literal from api into usable for human
    // description of episode
    fun getSeasonAndEpisode(episode: String?) =
        RickAndMortyApplication.getResources().getString(R.string.episode_season_title) +
                " ${getSeason(episode)}," +
                " ${
                    RickAndMortyApplication.getResources().getString(R.string.episode_number_title)
                }" +
                " ${getEpisode(episode)}"

    //fun getSeason(episode: String): String = Episode.getSeason(episode)
    // This method is for to get number of season from S0*E0* literal from api
    fun getSeason(episode: String?) = try {
        if (!episode.isNullOrEmpty() && episode.contains("S")) {
            val numbers = episode.split("S", "E")
            numbers[1].substring(getFirstNotZeroIndex(numbers[1]))
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }

    //fun getEpisode(episode: String): String = Episode.getEpisode(episode)
    // This method is for to get number of episode from S0*E0* literal from api
    fun getEpisode(episode: String?) = try {
        if (!episode.isNullOrEmpty() && episode.contains("E")) {
            val numbers = episode.split("S", "E")
            val episodePartIndex = if (episode.contains("S")) 2 else 1
            numbers[episodePartIndex].substring(getFirstNotZeroIndex(numbers[episodePartIndex]))
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }

    private fun getFirstNotZeroIndex(stringWithZerosAtStart: String): Int {
        var i = 0
        if (stringWithZerosAtStart.isNotEmpty()) {
            while (stringWithZerosAtStart[i] == '0' && i < stringWithZerosAtStart.length - 1) {
                i++
            }
        }
        return i
    }

    // This method is for to get S0*E0* literal from usable for human description of episode
    fun getEpisodeSymbol(seasonNumber: String, episodeNumber: String): String {
        val seasonLit = getEpisodePartLiteral(seasonNumber, "S")
        val episodeLit = getEpisodePartLiteral(episodeNumber, "E")
        return "$seasonLit$episodeLit"
    }

    private fun getEpisodePartLiteral(numberString: String?, startSymbol: String): String {
        return when (val number = if (numberString.isNullOrEmpty()) 0 else numberString.toInt()) {
            0 -> ""
            in 1..9 -> "${startSymbol}0$number"
            else -> "${startSymbol}$number"
        }
    }
}