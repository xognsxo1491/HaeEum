package com.example.swimming.data.profile

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.utils.UtilBase64Cipher

// 프로필 관련 저장소
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

    fun checkMessage(owner: LifecycleOwner, path1: String, path2: String) =
        dataSource.checkMessage(owner, path1, path2, UtilBase64Cipher.encode(id!!))

    fun updateMessageStatus(path1: String, path2: String, uuid: String) =
        dataSource.updateMessageStatus(path1, path2, UtilBase64Cipher.encode(id!!), uuid)

    fun deleteMessage(path1: String, path2: String, uuid: String) =
        dataSource.deleteMessage(path1, path2, UtilBase64Cipher.encode(id!!), uuid)

    fun showDictionary(uuid: String) =
        dataSource.showDictionary(uuid)

    fun showDictionaryImage(path: String) =
        dataSource.showDictionaryImage(path)
}