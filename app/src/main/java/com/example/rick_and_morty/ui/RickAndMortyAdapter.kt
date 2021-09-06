package com.example.rick_and_morty.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location
import com.example.data.entities.RickAndMortyEntity
import com.example.rick_and_morty.R
import com.example.rick_and_morty.viewmodel.EpisodeUtil

abstract class RickAndMortyAdapter<T : RickAndMortyEntity>(private val onClick: (T) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList = mutableListOf<T>()
    var isFullLoaded = true

    protected abstract fun getLayoutId(): Int
    protected abstract fun isOnline(): Boolean

    override fun getItemViewType(position: Int): Int =
        if (position == itemCount - 1 && !isFullLoaded && isOnline()) R.layout.progress_item
        else getLayoutId()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(
            LayoutInflater.from(parent.context).inflate(viewType, parent, false),
            viewType
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Binder<T>).bind(itemList[position], onClick)
    }

    override fun getItemCount(): Int = itemList.size

    fun update(items: MutableList<T>) {
        val diffResult = DiffUtil.calculateDiff(
            RickyAndMortyDiffUtil(itemList, items), false
        )
        itemList.clear()
        itemList.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    protected open fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderFactory.create(view, viewType)
    }

    internal interface Binder<T> {
        fun bind(data: T, onClick: (T) -> Unit)
    }
}

object ViewHolderFactory {
    fun create(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.character_item -> CharacterViewHolder(view)
            R.layout.location_item -> LocationViewHolder(view)
            R.layout.episode_item -> EpisodeViewHolder(view)
            R.layout.episode_item_2 -> EpisodeViewHolder(view)
            R.layout.progress_item -> ProgressViewHolder(view)
            else -> throw Exception(view.context.getString(R.string.no_appropriate_view_holder))
        }
    }
}

class CharacterViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    RickAndMortyAdapter.Binder<Character> {

    private val imageView = itemView.findViewById<ImageView>(R.id.character_image)
    private val nameView = itemView.findViewById<TextView>(R.id.character_name)
    private val speciesView = itemView.findViewById<TextView>(R.id.character_species)
    private val statusView = itemView.findViewById<TextView>(R.id.character_status)
    private val genderView = itemView.findViewById<TextView>(R.id.character_gender)
    private var currentCharacter: Character? = null

    override fun bind(character: Character, onClick: (Character) -> Unit) {
        currentCharacter = character
        itemView.setOnClickListener {
            currentCharacter?.let {
                onClick(it)
            }
        }
        Glide.with(itemView)
            .load(character.image)
            .into(imageView)
        nameView.text = character.name
        speciesView.text = character.species
        statusView.text = character.status
        genderView.text = character.gender
    }
}

class LocationViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    RickAndMortyAdapter.Binder<Location> {

    private val nameView = itemView.findViewById<TextView>(R.id.location_name)
    private val typeView = itemView.findViewById<TextView>(R.id.location_type)
    private val dimensionView = itemView.findViewById<TextView>(R.id.location_dimension)
    private var currentLocation: Location? = null

    override fun bind(location: Location, onClick: (Location) -> Unit) {
        currentLocation = location
        itemView.setOnClickListener {
            currentLocation?.let {
                onClick(it)
            }
        }
        nameView.text = location.name
        typeView.text = location.type
        dimensionView.text = location.dimension
    }

}

class EpisodeViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    RickAndMortyAdapter.Binder<Episode> {

    private val nameView = itemView.findViewById<TextView>(R.id.episode_name)
    private val episodeView = itemView.findViewById<TextView>(R.id.episode_episode)
    private val airDateView = itemView.findViewById<TextView>(R.id.episode_air_date)
    private var currentEpisode: Episode? = null

    override fun bind(episode: Episode, onClick: (Episode) -> Unit) {
        currentEpisode = episode
        itemView.setOnClickListener {
            currentEpisode?.let {
                onClick(it)
            }
        }
        nameView.text = episode.name
        episodeView.text = EpisodeUtil.getSeasonAndEpisode(episode.episode)
        airDateView.text = episode.airDate
    }

}

class ProgressViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    RickAndMortyAdapter.Binder<RickAndMortyEntity> {

    override fun bind(data: RickAndMortyEntity, onClick: (RickAndMortyEntity) -> Unit) {
    }
}

class RickyAndMortyLayoutManager {
    companion object {
        fun newInstance(
            context: Context?,
            adapter: RickAndMortyAdapter<out RickAndMortyEntity>
        ): GridLayoutManager {
            val layoutManager = GridLayoutManager(context, 2)
            layoutManager.spanSizeLookup = RickyAndMortySpanSizeLookup(adapter)
            return layoutManager
        }
    }
}

class RickyAndMortySpanSizeLookup(
    private val adapter: RickAndMortyAdapter<out RickAndMortyEntity>
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return when (adapter.getItemViewType(position)) {
            R.layout.progress_item -> 2
            else -> 1
        }
    }
}