package com.example.rick_and_morty.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import com.example.rick_and_morty.databinding.FragmentCharacterDetailsBinding
import com.example.rick_and_morty.ui.RickAndMortyAdapter
import com.example.rick_and_morty.ui.activities.DetailActivity
import com.example.rick_and_morty.viewmodel.CharacterDetailsViewModel

class CharacterDetailsFragment : Fragment() {

    private val viewModel: CharacterDetailsViewModel by activityViewModels {
        RickAndMortyApplication.getViewModelFactory()
    }
    private var characterId = -1
    private lateinit var episodeAdapter: RickAndMortyAdapter<Episode>
    private lateinit var onEpisodeClickListener: OnEpisodeClickListener
    private lateinit var onLocationClickListener: OnLocationClickListener
    private lateinit var binding: FragmentCharacterDetailsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onEpisodeClickListener = context as OnEpisodeClickListener
        onLocationClickListener = context as OnLocationClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        episodeAdapter =
            object : RickAndMortyAdapter<Episode>(onEpisodeClickListener::onEpisodeClick) {
                override fun getLayoutId(): Int = R.layout.episode_item_2
                override fun isOnline(): Boolean = viewModel.isOnline()
            }
        arguments?.let {
            characterId = it.getInt(CHARACTER_ID_EXTRA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_character_details, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = episodeAdapter
        binding.locListener = onLocationClickListener
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshLayout()
        viewModel.character.observe(viewLifecycleOwner, {
            if (activity is DetailActivity) {
                (activity as DetailActivity).toolbar.title = it?.name
            }
        })
        viewModel.episodes.observe(viewLifecycleOwner, { episodeAdapter.update(it) })
        viewModel.updateCharacter(characterId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dropCharacter()
    }

    private fun setRefreshLayout() =
        RickAndMortyApplication.setRefreshLayout(binding.characterDetailsRefreshLayout) {
            viewModel.updateCharacter(characterId)
        }

    interface OnEpisodeClickListener {
        fun onEpisodeClick(episode: Episode)
    }

    interface OnLocationClickListener {
        fun onLocationClick(location: Location)
    }

    companion object {
        const val TAG = "CharacterDetailsFragment"
        private const val CHARACTER_ID_EXTRA = "character_id_extra"

        fun newInstance(characterId: Int) = CharacterDetailsFragment().also {
            it.arguments = bundleOf(CHARACTER_ID_EXTRA to characterId)
        }
    }
}

@BindingAdapter("app:characterImage")
fun showCharacterImage(view: ImageView, character: Character?) {
    character?.let {
        Glide.with(view.context)
            .load(it.image)
            .into(view)
    }
}