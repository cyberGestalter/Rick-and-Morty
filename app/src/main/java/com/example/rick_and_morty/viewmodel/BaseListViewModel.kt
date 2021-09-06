package com.example.rick_and_morty.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.data.entities.RickAndMortyEntity
import com.example.rick_and_morty.R
import com.example.rick_and_morty.RickAndMortyApplication
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import java.util.*
import java.util.concurrent.TimeUnit

open class BaseListViewModel : ViewModel() {
    // This method is for to get next page number from link for pagination
    fun getNextPageNumberString(link: String?) = try {
        link?.split("?")?.get(1)
            ?.split("&")?.get(0)
            ?.split("=")?.get(1)
    } catch (e: Exception) {
        null
    }

    fun setNameChangeListening(
        observable: ObservableOnSubscribe<String>,
        observer: Consumer<String>
    ):
            Disposable {
        return Observable.create(observable)
            .map { text -> text.lowercase(Locale.getDefault()) }
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe(observer)
    }

    fun isOnline(): Boolean = RickAndMortyApplication.isOnline()

    fun getClearPic(context: Context) = ContextCompat.getDrawable(
        context,
        android.R.drawable.ic_menu_close_clear_cancel
    )

    fun getFilterPic(context: Context) = ContextCompat.getDrawable(context, R.drawable.ic_filter)

    inner class Merger<T : RickAndMortyEntity> {
        fun mergeLists(oldList: List<T>?, newList: List<T>?): List<T> {
            val resultList = mutableListOf<T>()
            if (newList != null) {
                oldList?.forEach { element ->
                    resultList.add(element)
                }
                resultList.addAll(newList)
            }
            return resultList
        }
    }
}