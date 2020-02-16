package com.example.swimming.ui.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityLoginBinding
import com.example.swimming.singletone.MainActivity
import com.example.swimming.ui.result.Result
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), Result, KodeinAware {
    override val kodein by kodein()

    private val factory: UserViewModelFactory by instance()
    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.result = this

        btn_login_register.setOnClickListener {
            val intent = Intent(this, ResisterActivity::class.java)
            startActivity(intent)
        }

        btn_login_find.setOnClickListener {
            val intent = Intent(this, FindActivity::class.java)
            startActivity(intent)
        }

        btn_login_login.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            viewModel.login()
        }

        viewModel.registerFormState.observe(this@LoginActivity, Observer {
            val registerState = it ?: return@Observer

            if (registerState.idError != null) {
                edit_login_id.error = getString(registerState.idError)
            }

            if (registerState.passwordError != null) {
                edit_login_password.error = getString(registerState.passwordError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    progress_login.visibility = View.VISIBLE

                } else
                    progress_login.visibility = View.INVISIBLE
            }
        })
    }

    override fun onSuccess() {
        progress_login.visibility = View.INVISIBLE

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
        progress_login.visibility = View.INVISIBLE
    }

    override fun onFailed() {
        Toast.makeText(this, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
        progress_login.visibility = View.INVISIBLE
    }
}
