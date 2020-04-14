package com.example.swimming.data.user

import android.content.Context
import com.example.swimming.etc.utils.UtilBase64Cipher

// 유저 정보 관련 저장소
class UserRepository(private val dataSource: UserDataSource, val context: Context) {
    private val pref = context.getSharedPreferences("Login",Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")
    private val editor = pref.edit()

    fun register(name: String, id: String, password: String, email: String) =
        dataSource.register(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(password), UtilBase64Cipher.encode(email))

    fun login(id: String, password: String) =
        dataSource.login(UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(password), editor)

    fun sendEmail(email: String, code: String) =
        dataSource.sendEmail(UtilBase64Cipher.encode(email), code)

    fun findId(name: String, email: String) =
        dataSource.findId(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(email))

    fun findPassword(name: String, id: String, email: String) =
        dataSource.findPassword(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(email))

    fun changePassword(password1: String, password3: String) =
        dataSource.changePassword(UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(password1), UtilBase64Cipher.encode(password3), editor)

    fun changeEmail(email: String, code1: String, code2: String) =
        dataSource.changeEmail(UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(email), code1, code2)

    fun sendEmailForChange(email1: String, email2: String, code: String) =
        dataSource.sendEmailForChange(UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(email1), email2, code)
}