package com.example.rick_and_morty.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.data.entities.Character
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import com.example.rick_and_morty.databinding.FragmentEpisodeDetailsBinding
import com.example.rick_and_morty.ui.RickAndMortyAdapter
import com.example.rick_and_morty.ui.activities.DetailActivity
import com.example.rick_and_morty.viewmodel.EpisodeDetailsViewModel

class EpisodeDetailsFragment : Fragment() {

    private val viewModel: EpisodeDetailsViewModel by activityViewModels {
        RickAndMortyApplication.getViewModelFactory()
    }
    private var episodeId = -1
    private lateinit var characterAdapter: RickAndMortyAdapter<Character>
    private lateinit var onCharacterClickListener: OnCharacterClickListener
    private lateinit var binding: FragmentEpisodeDetailsBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onCharacterClickListener = context as OnCharacterClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterAdapter =
            object : RickAndMortyAdapter<Character>(onCharacterClickListener::onCharacterClick) {
                override fun getLayoutId(): Int = R.layout.character_item
                override fun isOnline(): Boolean = viewModel.isOnline()
            }
        arguments?.let {
            episodeId = it.getInt(EPISODE_ID_EXTRA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_episode_details, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = characterAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRefreshLayout()
        viewModel.episode.observe(viewLifecycleOwner, {
            if (activity is DetailActivity) {
                (activity as DetailActivity).toolbar.title = it?.name
            }
        })
        viewModel.characters.observe(viewLifecycleOwner, { characterAdapter.update(it) })
        viewModel.updateEpisode(episodeId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.dropEpisode()
    }

    private fun setRefreshLayout() =
        RickAndMortyApplication.setRefreshLayout(binding.episodeDetailsRefreshLayout) {
            viewModel.updateEpisode(episodeId)
        }

    interface OnCharacterClickListener {
        fun onCharacterClick(character: Character)
    }

    companion object {
        const val TAG = "EpisodeDetailsFragment"
        private const val EPISODE_ID_EXTRA = "episode_id_extra"

        fun newInstance(episodeId: Int) = EpisodeDetailsFragment().also {
            it.arguments = bundleOf(EPISODE_ID_EXTRA to episodeId)
        }
    }
}