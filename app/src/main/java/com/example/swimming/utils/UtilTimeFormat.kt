package com.example.swimming.utils

// 시간 변환
object UtilTimeFormat {
    fun formatting(regTime: Long): String {
        val sec = 60
        val min = 60
        val hour = 24
        val day = 30
        val month = 12

        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - regTime) / 1000
        val message: String?

        when {
            diffTime < sec -> {
                message = "방금 전"
            }
            sec.let { diffTime /= it; diffTime } < min -> {
                message = diffTime.toString() + "분 전"
            }
            min.let { diffTime /= it; diffTime } < hour -> {
                message = diffTime.toString() + "시간 전"
            }
            hour.let { diffTime /= it; diffTime } < day -> {
                message = diffTime.toString() + "일 전"
            }
            day.let { diffTime /= it; diffTime } < month -> {
                message = diffTime.toString() + "달 전"
            }
            else -> {
                message = diffTime.toString() + "년 전"
            }
        }
        return message
    }
}