package com.example.swimming.etc.utils

import android.util.Base64

// Base64 암호화
object UtilBase64Cipher {
    fun encode(content: String): String {
        return Base64.encodeToString(content.toByteArray(), Base64.NO_WRAP)
    }

    fun decode(content: String): String {
        return String(Base64.decode(content, Base64.NO_WRAP))
    }
}