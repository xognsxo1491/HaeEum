package com.example.swimming.data.user

import android.content.Context
import com.example.swimming.utils.UtilBase64Cipher

class UserRepository(private val dataSource: UserDataSource, val context: Context) {

    private val pref = context.getSharedPreferences("Login",Context.MODE_PRIVATE)
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

}