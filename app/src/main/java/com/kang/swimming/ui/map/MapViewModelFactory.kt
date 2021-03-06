package com.kang.swimming.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.data.map.MapDataSource
import com.kang.swimming.data.map.MapRepository

@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                repository = MapRepository(MapDataSource(), context = context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}