package com.kang.swimming.etc.utils

import java.text.SimpleDateFormat
import java.util.*

// 시간 병경
object UtilDateFormat {
    fun formatting(long: Long) : String {

        val date = Date(long)
        val format = SimpleDateFormat("MM / dd HH:mm", Locale.KOREA)

        return format.format(date)
    }
}