package com.example.swimming.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.data.profile.ProfileDataSource
import com.example.swimming.data.profile.ProfileRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                repository = ProfileRepository(ProfileDataSource(), context = context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}