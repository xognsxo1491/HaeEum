package com.example.swimming.singletone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.swimming.R
import com.example.swimming.ui.user.LoginActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

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
        }, 1000)
    }
}
