package com.example.swimming.ui.result

interface Result {
    fun onSuccess()
    fun onFailed()
    fun onError()
}