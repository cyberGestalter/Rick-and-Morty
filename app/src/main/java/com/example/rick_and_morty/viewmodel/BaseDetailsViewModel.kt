package com.example.rick_and_morty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rick_and_morty.RickAndMortyApplication
import java.text.SimpleDateFormat
import java.util.*

open class BaseDetailsViewModel : ViewModel() {

    protected val _created = object : MutableLiveData<String?>() {
        override fun setValue(value: String?) {
            super.setValue(getDateInSimpleFormat(value))
        }
    }
    val created: LiveData<String?>
        get() = _created

    // This method is for to get id of character, location or episode from link
    protected fun getIdFromLink(link: String) = try {
        link.split("/").last().toInt()
    } catch (e: Exception) {
        0
    }

    // This method is for to get date in format which is usable for human
    private fun getDateInSimpleFormat(date: String?): String = try {
        val simpleDateFormatFrom = SimpleDateFormat(DATE_FROM_TEMPLATE, Locale.getDefault())
        val simpleDateFormatTo = SimpleDateFormat(DATE_TO_TEMPLATE, Locale.US)
        val srcDate = simpleDateFormatFrom.parse(date!!)
        simpleDateFormatTo.format(srcDate!!)
    } catch (e: Exception) {
        ""
    }

    fun isOnline(): Boolean = RickAndMortyApplication.isOnline()

    companion object {
        private const val DATE_FROM_TEMPLATE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private const val DATE_TO_TEMPLATE = "MMMM d, yyyy"
    }
}