package com.example.rick_and_morty.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.rick_and_morty.R
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                MainActivity.newIntent(this@SplashScreenActivity)
                finish()
            }, SPLASH_SCREEN_DURATION
        )
    }

    companion object {
        const val SPLASH_SCREEN_DURATION = 2000L
    }
}