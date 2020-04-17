package com.kang.swimming.ui.result

interface Result {
    fun onSuccess()
    fun onFailed()
    fun onError()
}