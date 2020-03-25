package com.example.swimming.data.profile

import android.content.Context
import android.content.Intent

class ProfileRepository(private val dataSource: ProfileDataSource, val context: Context) {

    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")
    private val editor = pref.edit()

    fun checkToken() =
        dataSource.checkToken(editor)

    fun setProfile() =
        dataSource.setProfile(id!!)

    fun logout() =
        dataSource.logout(editor)
}
