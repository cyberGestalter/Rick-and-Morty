package com.example.rick_and_morty.dagger

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    fun context(): Context = context.applicationContext
}