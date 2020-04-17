package com.kang.swimming.etc.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.kang.swimming.R
import com.kang.swimming.ui.profile.MainActivity
import com.kang.swimming.ui.user.LoginActivity

// 로딩 화면
class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        startLoading()
    }

    private fun startLoading() {
        val pref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val id = pref.getString("Id","")

        val handler = Handler()
        handler.postDelayed({
            if (id != "") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }, 500)
    }
}
