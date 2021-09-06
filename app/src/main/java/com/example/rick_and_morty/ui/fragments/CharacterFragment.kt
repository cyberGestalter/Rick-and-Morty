package com.example.rick_and_morty.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.data.entities.Character
import com.example.data.entities.CharacterList
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import com.example.rick_and_morty.databinding.FragmentCharacterBinding
import com.example.rick_and_morty.ui.RickAndMortyAdapter
import com.example.rick_and_morty.ui.RickyAndMortyLayoutManager
import com.example.rick_and_morty.viewmodel.CharacterViewModel
import com.google.android.material.card.MaterialCardView
import io.reactivex.rxjava3.disposables.Disposable

class CharacterFragment : Fragment() {

    private val viewModel: CharacterViewModel by activityViewModels {
        RickAndMortyApplication.getViewModelFactory()
    }
    private lateinit var adapter: RickAndMortyAdapter<Character>
    private lateinit var onCharacterClickListener: OnCharacterClickListener
    private lateinit var binding: FragmentCharacterBinding
    private lateinit var characterRecyclerView: RecyclerView
    private lateinit var searchName: EditText
    private lateinit var searchPanel: MaterialCardView
    private lateinit var searchSpecies: EditText
    private lateinit var searchType: EditText
    private lateinit var searchStatusGroup: RadioGroup
    private lateinit var searchGendersGroup: RadioGroup
    private lateinit var filterBtn: ImageView
    private lateinit var searchNameDispose: Disposable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onCharacterClickListener = context as OnCharacterClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter =
            object : RickAndMortyAdapter<Character>(onCharacterClickListener::onCharacterClick) {
                override fun getLayoutId(): Int = R.layout.character_item
                override fun isOnline(): Boolean = viewModel.isOnline()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_character, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.layoutManager = RickyAndMortyLayoutManager.newInstance(context, adapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchPanel = binding.fragmentCharacterSearchPanel.characterSearchPanel
        searchSpecies = binding.fragmentCharacterSearchPanel.characterSearchSpeciesTitle
        searchSpecies.setText(viewModel.speciesFilter)
        searchType = binding.fragmentCharacterSearchPanel.characterSearchTypeTitle
        searchType.setText(viewModel.typeFilter)
        searchStatusGroup = binding.fragmentCharacterSearchPanel.characterSearchStatuses
        setCheckedInRadioGroup(searchStatusGroup, viewModel.statusFilter)
        searchGendersGroup = binding.fragmentCharacterSearchPanel.characterSearchGenders
        setCheckedInRadioGroup(searchGendersGroup, viewModel.genderFilter)

        setRecyclerView()
        setSearchByName()
        setFilterBtn()
        setFindBtn()
        setResetBtn()
        setRefreshLayout()

        viewModel.characterList.observe(viewLifecycleOwner,
            { newList -> newList?.let { updateForNewList(newList) } }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        searchNameDispose.dispose()
    }

    private fun setCheckedInRadioGroup(group: RadioGroup, buttonText: String?) {
        for (i in 0 until group.childCount) {
            val radioButton = group.getChildAt(i) as RadioButton
            if (radioButton.text == buttonText) {
                radioButton.isChecked = true
                break
            }
        }
    }

    private fun setRecyclerView() {
        characterRecyclerView = binding.characterList
        characterRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && viewModel.isOnline()) {
                    viewModel.addNextCharacters()
                }
            }
        })
    }

    private fun setSearchByName() {
        searchName = binding.fragmentCharacterSearchName.searchText
        searchName.setText(viewModel.nameFilter)
        searchNameDispose = viewModel.setNameChangeListening(
            { subscriber ->
                searchName.doOnTextChanged { text, _, _, _ -> subscriber.onNext(text.toString()) }
            },
            { text ->
                viewModel.filterListByName(text)
                characterRecyclerView.smoothScrollToPosition(0)
            }
        )
    }

    private fun setFilterBtn() {
        filterBtn = binding.fragmentCharacterSearchName.searchClearContentBtn
        filterBtn.setOnClickListener {
            changeSearchPanelVisibility()
        }
    }

    private fun setFindBtn() = binding.fragmentCharacterSearchPanel.characterSearchFindButton
        .setOnClickListener {
            val nameFilter = searchName.text.toString()
            val checkedStatusId = searchStatusGroup.checkedRadioButtonId
            val statusFilter = if (checkedStatusId != -1) {
                searchPanel.findViewById<RadioButton>(checkedStatusId).text.toString()
            } else {
                ""
            }
            val speciesFilter = searchSpecies.text.toString()
            val typeFilter = searchType.text.toString()
            val checkedGenderId = searchGendersGroup.checkedRadioButtonId
            val genderFilter = if (checkedGenderId != -1) {
                searchPanel.findViewById<RadioButton>(checkedGenderId).text.toString()
            } else {
                ""
            }
            changeSearchPanelVisibility()
            viewModel.filterList(nameFilter, statusFilter, speciesFilter, typeFilter, genderFilter)
            characterRecyclerView.smoothScrollToPosition(0)
        }

    private fun setResetBtn() = binding.fragmentCharacterSearchPanel.characterSearchResetButton
        .setOnClickListener {
            changeSearchPanelVisibility()
            dropFieldValues()
        }

    private fun setRefreshLayout() = RickAndMortyApplication.setRefreshLayout(
        binding.characterRefreshLayout,
        this::dropFieldValues
    )

    private fun updateForNewList(newList: CharacterList?) = newList?.let {
        adapter.isFullLoaded = it.info.next == null
        adapter.update(it.results.toMutableList())
        changeNoElementsVisibility(it.results)
    }

    private fun dropFieldValues() {
        searchName.text = null
        searchStatusGroup.clearCheck()
        searchSpecies.text = null
        searchType.text = null
        searchGendersGroup.clearCheck()
        viewModel.dropFiltersAndUpdateCharacters()
        characterRecyclerView.smoothScrollToPosition(0)
    }

    private fun changeSearchPanelVisibility() = if (searchPanel.visibility != View.VISIBLE) {
        filterBtn.setImageDrawable(viewModel.getClearPic(requireContext()))
        searchPanel.visibility = View.VISIBLE
    } else {
        filterBtn.setImageDrawable(viewModel.getFilterPic(requireContext()))
        searchPanel.visibility = View.GONE
    }

    private fun changeNoElementsVisibility(list: List<Character>) = if (list.isEmpty())
        binding.characterNoCharacterFound.visibility = View.VISIBLE
    else
        binding.characterNoCharacterFound.visibility = View.GONE

    interface OnCharacterClickListener {
        fun onCharacterClick(character: Character)
    }

    companion object {
        const val TAG = "CharacterFragment"
        fun newInstance() = CharacterFragment()
    }
}