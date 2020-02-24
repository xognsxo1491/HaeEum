package com.example.swimming.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

// Progress Dialog 띄우기
fun UtilShowDialog(context: Context, message: String): AlertDialog {
    val padding = 30
    val layout = LinearLayout(context)

    layout.orientation = LinearLayout.HORIZONTAL
    layout.setPadding(padding, padding, padding, padding)
    layout.gravity = Gravity.CENTER

    var params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    params.gravity = Gravity.CENTER
    layout.layoutParams = params

    val progressBar = ProgressBar(context)
    progressBar.isIndeterminate = true
    progressBar.setPadding(0, 0, padding, 0)
    progressBar.layoutParams = params

    params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.gravity = Gravity.CENTER

    val textView = TextView(context)
    textView.text = message
    textView.setTextColor(Color.parseColor("#000000"))
    textView.textSize = 12.toFloat()
    textView.layoutParams = params

    layout.addView(progressBar)
    layout.addView(textView)

    val builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    builder.setView(layout)

    val dialog = builder.create()
    val window = dialog.window
    if (window != null) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams
    }

    return dialog
}