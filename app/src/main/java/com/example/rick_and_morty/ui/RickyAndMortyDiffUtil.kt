package com.example.rick_and_morty.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.data.entities.RickAndMortyEntity

class RickyAndMortyDiffUtil<T : RickAndMortyEntity>(
    private val oldList: MutableList<T>,
    private val newList: MutableList<T>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] === newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}