package com.example.swimming.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull

// 키보드 내리기
object UtilKeyboard {
    fun hideKeyboard(@NonNull activity: Activity) {
        val view = activity.currentFocus

        if (view != null) {
            val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}