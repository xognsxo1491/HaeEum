package com.example.swimming.data.profile

import android.content.Context
import android.content.Intent

class ProfileRepository(private val dataSource: ProfileDataSource) {

    fun setProfile(context: Context) =
        dataSource.setProfile(context)

    fun uploadProfileImage(id: String, data: Intent?) =
        dataSource.uploadProfileImage(id, data)

    fun downloadProfileImage(id: String) =
        dataSource.downloadProfileImage(id)

    fun logout(context: Context) =
        dataSource.logout(context)
}
