package com.example.swimming.ui.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.data.user.UserDataSource
import com.example.swimming.data.user.UserRepository

class UserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(
                repository = UserRepository(
                    UserDataSource()
                ), context = context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}