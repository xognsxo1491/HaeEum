package com.kang.swimming.ui.board

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.data.board.BoardDataSource
import com.kang.swimming.data.board.BoardRepository

@Suppress("UNCHECKED_CAST")
class BoardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BoardViewModel::class.java)) {
            return BoardViewModel(
                repository = BoardRepository(BoardDataSource(), context = context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}