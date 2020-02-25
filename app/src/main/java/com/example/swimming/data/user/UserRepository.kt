package com.example.swimming.data.user

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import com.example.swimming.utils.UtilBase64Cipher
import io.reactivex.Single

class UserRepository(private val dataSource: UserDataSource) {

    fun register(name: String, id: String, password: String, email: String) =
        dataSource.register(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(password), UtilBase64Cipher.encode(email))

    fun login(id: String, password: String, context: Context) =
        dataSource.login(UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(password), context)

    fun sendEmail(email: String, code: String) =
        dataSource.sendEmail(UtilBase64Cipher.encode(email), code)

    fun findId(name: String, email: String) =
        dataSource.findId(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(email))

    fun findPassword(name: String, id: String, email: String) =
        dataSource.findPassword(UtilBase64Cipher.encode(name), UtilBase64Cipher.encode(id), UtilBase64Cipher.encode(email))

}