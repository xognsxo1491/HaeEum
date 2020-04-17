package com.example.swimming.data.profile

import android.content.Context
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.etc.utils.UtilBase64Cipher

// 프로필 관련 저장소
class ProfileRepository(private val dataSource: ProfileDataSource, val context: Context) {
    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")
    private val email = pref.getString("email", "")
    private val password = pref.getString("password", "")
    private val editor = pref.edit()

    fun checkToken() =
        dataSource.checkToken(editor)

    fun setProfile() =
        dataSource.setProfile(id!!, UtilBase64Cipher.decode(email!!), password!!)

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

    // 댓글 알림 푸쉬 설정
    fun checkAlarm(switch: SwitchCompat) {
        if (switch.isChecked) {
            editor.putBoolean("alarm", true).apply()
        } else {
            editor.putBoolean("alarm", false).apply()
        }
    }

    // 알림 알림 탭 설정
    fun checkTab(switch: SwitchCompat) {
        if (switch.isChecked) {
            editor.putBoolean("tab", true).apply()
        } else {
            editor.putBoolean("tab", false).apply()
        }
    }

    // 댓글 알림 푸쉬 상태 불러오기
    fun loadStatusAlarm(): Boolean {
        return pref.getBoolean("alarm", true)
    }

    fun loadStatusTab(): Boolean {
        return pref.getBoolean("tab", true)
    }
}