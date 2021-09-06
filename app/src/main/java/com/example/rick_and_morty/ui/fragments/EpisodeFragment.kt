package com.example.rick_and_morty.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.data.entities.Episode
import com.example.data.entities.EpisodeList
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import com.example.rick_and_morty.databinding.FragmentEpisodeBinding
import com.example.rick_and_morty.ui.RickAndMortyAdapter
import com.example.rick_and_morty.ui.RickyAndMortyLayoutManager
import com.example.rick_and_morty.viewmodel.EpisodeViewModel
import com.google.android.material.card.MaterialCardView
import io.reactivex.rxjava3.disposables.Disposable

class EpisodeFragment : Fragment() {

    private val viewModel: EpisodeViewModel by activityViewModels {
        RickAndMortyApplication.getViewModelFactory()
    }
    private lateinit var onEpisodeClickListener: OnEpisodeClickListener
    private lateinit var adapter: RickAndMortyAdapter<Episode>
    private lateinit var binding: FragmentEpisodeBinding
    private lateinit var episodeRecyclerView: RecyclerView
    private lateinit var searchName: EditText
    private lateinit var searchPanel: MaterialCardView
    private lateinit var searchSeason: EditText
    private lateinit var searchEpisode: EditText
    private lateinit var filterBtn: ImageView
    private lateinit var searchNameDispose: Disposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onEpisodeClickListener = context as OnEpisodeClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = object : RickAndMortyAdapter<Episode>(onEpisodeClickListener::onEpisodeClick) {
            override fun getLayoutId(): Int = R.layout.episode_item
            override fun isOnline(): Boolean = viewModel.isOnline()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_episode, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.layoutManager = RickyAndMortyLayoutManager.newInstance(context, adapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchPanel = binding.fragmentEpisodeSearchPanel.episodeSearchPanel
        searchSeason = binding.fragmentEpisodeSearchPanel.episodeSearchSeasonTitle
        searchSeason.setText(viewModel.getSeason())
        searchEpisode = binding.fragmentEpisodeSearchPanel.episodeSearchEpisodeTitle
        searchEpisode.setText(viewModel.getEpisode())

        setRecyclerView()
        setSearchByName()
        setFilterBtn()
        setFindBtn()
        setResetBtn()
        setRefreshLayout()

        viewModel.episodeList.observe(viewLifecycleOwner,
            { newList -> newList?.let { updateForNewList(newList) } })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchNameDispose.dispose()
    }

    private fun setRecyclerView() {
        episodeRecyclerView = binding.episodeList
        episodeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && viewModel.isOnline()) {
                    viewModel.addNextEpisodes()
                }
            }
        })
    }

    private fun setSearchByName() {
        searchName = binding.fragmentEpisodeSearchName.searchText
        searchName.setText(viewModel.nameFilter)
        searchNameDispose = viewModel.setNameChangeListening(
            { subscriber ->
                searchName.doOnTextChanged { text, _, _, _ -> subscriber.onNext(text.toString()) }
            },
            { text ->
                viewModel.filterListByName(text)
                episodeRecyclerView.smoothScrollToPosition(0)
            }
        )
    }

    private fun setFilterBtn() {
        filterBtn = binding.fragmentEpisodeSearchName.searchClearContentBtn
        filterBtn.setOnClickListener { changeSearchPanelVisibility() }
    }

    private fun setFindBtn() = binding.fragmentEpisodeSearchPanel.episodeSearchFindButton
        .setOnClickListener {
            val nameFilter = searchName.text.toString()
            val episodeFilter =
                viewModel.getEpisodeSymbol(
                    searchSeason.text.toString(),
                    searchEpisode.text.toString()
                )
            changeSearchPanelVisibility()
            viewModel.filterList(nameFilter, episodeFilter)
            episodeRecyclerView.smoothScrollToPosition(0)
        }

    private fun setResetBtn() = binding.fragmentEpisodeSearchPanel.episodeSearchResetButton
        .setOnClickListener {
            changeSearchPanelVisibility()
            dropFieldValues()
        }

    private fun setRefreshLayout() = RickAndMortyApplication.setRefreshLayout(
        binding.episodeRefreshLayout,
        this::dropFieldValues
    )

    private fun updateForNewList(newList: EpisodeList?) = newList?.let {
        adapter.isFullLoaded = it.info.next == null
        adapter.update(it.results.toMutableList())
        changeNoElementsVisibility(it.results)
    }

    private fun dropFieldValues() {
        searchName.text = null
        searchSeason.text = null
        searchEpisode.text = null
        viewModel.dropFiltersAndUpdateEpisodes()
        episodeRecyclerView.smoothScrollToPosition(0)
    }

    private fun changeSearchPanelVisibility() = if (searchPanel.visibility != View.VISIBLE) {
        filterBtn.setImageDrawable(viewModel.getClearPic(requireContext()))
        searchPanel.visibility = View.VISIBLE
    } else {
        filterBtn.setImageDrawable(viewModel.getFilterPic(requireContext()))
        searchPanel.visibility = View.GONE
    }

    private fun changeNoElementsVisibility(list: List<Episode>) = if (list.isEmpty())
        binding.episodeNoEpisodeFound.visibility = View.VISIBLE
    else
        binding.episodeNoEpisodeFound.visibility = View.GONE

    interface OnEpisodeClickListener {
        fun onEpisodeClick(episode: Episode)
    }

    companion object {
        const val TAG = "EpisodeFragment"
        fun newInstance() = EpisodeFragment()
    }
}