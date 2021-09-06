package com.example.rick_and_morty.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.data.entities.Character
import com.example.data.entities.Episode
import com.example.data.entities.Location
import com.example.rick_and_morty.R
import com.example.rick_and_morty.ui.fragments.CharacterFragment
import com.example.rick_and_morty.ui.fragments.EpisodeFragment
import com.example.rick_and_morty.ui.fragments.LocationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity :
    AppCompatActivity(),
    CharacterFragment.OnCharacterClickListener,
    LocationFragment.OnLocationClickListener,
    EpisodeFragment.OnEpisodeClickListener {

    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationMenu: BottomNavigationView
    private lateinit var characterFragment: CharacterFragment
    private lateinit var locationFragment: LocationFragment
    private lateinit var episodeFragment: EpisodeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        characterFragment = CharacterFragment.newInstance()
        locationFragment = LocationFragment.newInstance()
        episodeFragment = EpisodeFragment.newInstance()

        if (savedInstanceState != null) {
            chooseFragment(savedInstanceState.getInt(CHECKED_ID))
        } else {
            toolbar.title = getString(R.string.character_title)
            setCurrentFragment(characterFragment, CharacterFragment.TAG)
        }

        bottomNavigationMenu = findViewById(R.id.bottom_navigation_menu)
        bottomNavigationMenu.setOnItemSelectedListener {
            chooseFragment(it.itemId)
            true
        }
    }

    private fun chooseFragment(menuItemId: Int) {
        when (menuItemId) {
            R.id.character -> {
                toolbar.title = getString(R.string.character_title)
                setCurrentFragment(characterFragment, CharacterFragment.TAG)
            }
            R.id.location -> {
                toolbar.title = getString(R.string.location_title)
                setCurrentFragment(locationFragment, LocationFragment.TAG)
            }
            R.id.episode -> {
                toolbar.title = getString(R.string.episode_title)
                setCurrentFragment(episodeFragment, EpisodeFragment.TAG)
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment, fragmentTag: String) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragment_container, fragment, fragmentTag)
            commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CHECKED_ID, bottomNavigationMenu.selectedItemId)
    }

    override fun onCharacterClick(character: Character) {
        DetailActivity.newIntent(this, character.id, DetailActivity.CHARACTER_TYPE)
    }

    override fun onLocationClick(location: Location) {
        DetailActivity.newIntent(this, location.id, DetailActivity.LOCATION_TYPE)
    }

    override fun onEpisodeClick(episode: Episode) {
        DetailActivity.newIntent(this, episode.id, DetailActivity.EPISODE_TYPE)
    }

    companion object {
        const val CHECKED_ID = "checked_id"

        fun newIntent(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}