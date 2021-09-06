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
import com.example.data.entities.Location
import com.example.data.entities.LocationList
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import com.example.rick_and_morty.databinding.FragmentLocationBinding
import com.example.rick_and_morty.ui.RickAndMortyAdapter
import com.example.rick_and_morty.ui.RickyAndMortyLayoutManager
import com.example.rick_and_morty.viewmodel.LocationViewModel
import com.google.android.material.card.MaterialCardView
import io.reactivex.rxjava3.disposables.Disposable

class LocationFragment : Fragment() {

    private val viewModel: LocationViewModel by activityViewModels {
        RickAndMortyApplication.getViewModelFactory()
    }
    private lateinit var onLocationClickListener: OnLocationClickListener
    private lateinit var adapter: RickAndMortyAdapter<Location>
    private lateinit var binding: FragmentLocationBinding
    private lateinit var locationRecyclerView: RecyclerView
    private lateinit var searchName: EditText
    private lateinit var searchPanel: MaterialCardView
    private lateinit var searchType: EditText
    private lateinit var searchDimension: EditText
    private lateinit var filterBtn: ImageView
    private lateinit var searchNameDispose: Disposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onLocationClickListener = context as OnLocationClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = object : RickAndMortyAdapter<Location>(onLocationClickListener::onLocationClick) {
            override fun getLayoutId(): Int = R.layout.location_item
            override fun isOnline(): Boolean = viewModel.isOnline()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_location, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.layoutManager = RickyAndMortyLayoutManager.newInstance(context, adapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchPanel = binding.fragmentLocationSearchPanel.locationSearchPanel
        searchType = binding.fragmentLocationSearchPanel.locationSearchTypeTitle
        searchType.setText(viewModel.typeFilter)
        searchDimension = binding.fragmentLocationSearchPanel.locationSearchDimensionTitle
        searchDimension.setText(viewModel.dimensionFilter)

        setRecyclerView()
        setSearchByName()
        setFilterBtn()
        setFindBtn()
        setResetBtn()
        setRefreshLayout()

        viewModel.locationList.observe(viewLifecycleOwner,
            { newList -> newList?.let { updateForNewList(newList) } })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchNameDispose.dispose()
    }

    private fun setRecyclerView() {
        locationRecyclerView = binding.locationList
        locationRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && viewModel.isOnline()) {
                    viewModel.addNextLocations()
                }
            }
        })
    }

    private fun setSearchByName() {
        searchName = binding.fragmentLocationSearchName.searchText
        searchName.setText(viewModel.nameFilter)
        searchNameDispose = viewModel.setNameChangeListening(
            { subscriber ->
                searchName.doOnTextChanged { text, _, _, _ -> subscriber.onNext(text.toString()) }
            },
            { text ->
                viewModel.filterListByName(text)
                locationRecyclerView.smoothScrollToPosition(0)
            }
        )
    }

    private fun setFilterBtn() {
        filterBtn = binding.fragmentLocationSearchName.searchClearContentBtn
        filterBtn.setOnClickListener { changeSearchPanelVisibility() }
    }

    private fun setFindBtn() = binding.fragmentLocationSearchPanel.locationSearchFindButton
        .setOnClickListener {
            val nameFilter = searchName.text.toString()
            val typeFilter = searchType.text.toString()
            val dimensionFilter = searchDimension.text.toString()
            changeSearchPanelVisibility()
            viewModel.filterList(nameFilter, typeFilter, dimensionFilter)
            locationRecyclerView.smoothScrollToPosition(0)
        }

    private fun setResetBtn() = binding.fragmentLocationSearchPanel.locationSearchResetButton
        .setOnClickListener {
            changeSearchPanelVisibility()
            dropFieldValues()
        }

    private fun setRefreshLayout() =
        RickAndMortyApplication.setRefreshLayout(
            binding.locationRefreshLayout,
            this::dropFieldValues
        )

    private fun updateForNewList(newList: LocationList?) = newList?.let {
        adapter.isFullLoaded = it.info.next == null
        adapter.update(it.results.toMutableList())
        changeNoElementsVisibility(it.results)
    }

    private fun dropFieldValues() {
        searchName.text = null
        searchType.text = null
        searchDimension.text = null
        viewModel.dropFiltersAndUpdateLocations()
        locationRecyclerView.smoothScrollToPosition(0)
    }

    private fun changeSearchPanelVisibility() = if (searchPanel.visibility != View.VISIBLE) {
        filterBtn.setImageDrawable(viewModel.getClearPic(requireContext()))
        searchPanel.visibility = View.VISIBLE
    } else {
        filterBtn.setImageDrawable(viewModel.getFilterPic(requireContext()))
        searchPanel.visibility = View.GONE
    }

    private fun changeNoElementsVisibility(list: List<Location>) = if (list.isEmpty())
        binding.locationNoLocationFound.visibility = View.VISIBLE
    else
        binding.locationNoLocationFound.visibility = View.GONE

    interface OnLocationClickListener {
        fun onLocationClick(location: Location)
    }

    companion object {
        const val TAG = "LocationFragment"
        fun newInstance() = LocationFragment()
    }
}