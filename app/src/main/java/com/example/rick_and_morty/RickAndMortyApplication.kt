package com.example.rick_and_morty

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rick_and_morty.dagger.ContextModule
import com.example.rick_and_morty.dagger.DaggerViewModelFactoryComponent
import com.example.rick_and_morty.dagger.ViewModelFactoryComponent

class RickAndMortyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        viewModelFactoryComponent = DaggerViewModelFactoryComponent.builder()
            .contextModule(ContextModule(this.applicationContext))
            .build()
    }

    companion object {
        private lateinit var app: RickAndMortyApplication
        private lateinit var viewModelFactoryComponent: ViewModelFactoryComponent

        fun getResources(): Resources = app.resources
        fun getViewModelFactory() = viewModelFactoryComponent.getFactory()

        // This method is for to get information internet connection is available or not
        fun isOnline(): Boolean {
            val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val netInfo = cm.getNetworkCapabilities(cm.activeNetwork)
                netInfo != null
            } else {
                val netInfo = cm.activeNetworkInfo
                netInfo != null && netInfo.isConnectedOrConnecting
            }
        }

        fun setRefreshLayout(layout: SwipeRefreshLayout, actionOnSwipe: () -> Unit) {
            layout.setOnRefreshListener {
                layout.isRefreshing = false
                if (isOnline()) {
                    actionOnSwipe.invoke()
                } else {
                    Toast.makeText(
                        app.applicationContext,
                        getResources().getString(R.string.no_connection_to_the_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}