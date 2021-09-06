package com.example.rick_and_morty.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location
import com.example.rick_and_morty.R
import com.example.rick_and_morty.ui.fragments.CharacterDetailsFragment
import com.example.rick_and_morty.ui.fragments.EpisodeDetailsFragment
import com.example.rick_and_morty.ui.fragments.LocationDetailsFragment

class DetailActivity :
    AppCompatActivity(),
    CharacterDetailsFragment.OnEpisodeClickListener,
    CharacterDetailsFragment.OnLocationClickListener,
    LocationDetailsFragment.OnCharacterClickListener,
    EpisodeDetailsFragment.OnCharacterClickListener {

    private lateinit var _toolbar: Toolbar
    val toolbar: Toolbar
        get() = _toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        _toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            var id = 0
            var entityType = 0

            intent.extras?.let {
                id = it.getInt(DETAIL_ID)
                entityType = it.getInt(ENTITY_TYPE)
            }

            when (entityType) {
                CHARACTER_TYPE -> setFragment(
                    CharacterDetailsFragment.newInstance(id),
                    CharacterDetailsFragment.TAG
                )
                LOCATION_TYPE -> setFragment(
                    LocationDetailsFragment.newInstance(id),
                    LocationDetailsFragment.TAG
                )
                EPISODE_TYPE -> setFragment(
                    EpisodeDetailsFragment.newInstance(id),
                    EpisodeDetailsFragment.TAG
                )
                else -> throw Exception(getString(R.string.no_such_fragment_type))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLocationClick(location: Location) {
        setFragment(LocationDetailsFragment.newInstance(location.id), LocationDetailsFragment.TAG)
    }

    override fun onCharacterClick(character: Character) {
        setFragment(
            CharacterDetailsFragment.newInstance(character.id),
            CharacterDetailsFragment.TAG
        )
    }

    override fun onEpisodeClick(episode: Episode) {
        setFragment(EpisodeDetailsFragment.newInstance(episode.id), EpisodeDetailsFragment.TAG)
    }

    private fun setFragment(fragment: Fragment, fragmentTag: String) {
        val detailFragmentCount = supportFragmentManager.fragments.size
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragment_detail_container, fragment, fragmentTag)
            if (detailFragmentCount != 0) {
                addToBackStack(fragmentTag)
            }
            commit()
        }
    }

    companion object {
        const val DETAIL_ID = "detail_id"
        const val ENTITY_TYPE = "entity_type"

        const val CHARACTER_TYPE = 1
        const val LOCATION_TYPE = 2
        const val EPISODE_TYPE = 3

        fun newIntent(context: Context, id: Int, entityType: Int) {
            val intent = Intent(context, DetailActivity::class.java).run {
                putExtra(DETAIL_ID, id)
                putExtra(ENTITY_TYPE, entityType)
            }
            context.startActivity(intent)
        }
    }
}