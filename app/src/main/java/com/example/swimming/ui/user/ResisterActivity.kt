package com.example.swimming.ui.user

import android.app.AlertDialog
import android.os.Bundle
import android.os.StrictMode
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.ui.result.UserActionResult
import com.example.swimming.databinding.ActivityResisterBinding
import com.example.swimming.utils.UtilKeyboard
import kotlinx.android.synthetic.main.activity_resister.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ResisterActivity : AppCompatActivity(), UserActionResult, KodeinAware {
    override val kodein by kodein()

    private val factory: UserViewModelFactory by instance()
    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val binding: ActivityResisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_resister)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setSupportActionBar(binding.toolbarRegister)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel
        viewModel.userActionResult = this

        viewModel.registerFormState.observe(this@ResisterActivity, Observer {
            val registerState = it ?: return@Observer

            if (registerState.nameError != null) {
                edit_register_name.error = getString(registerState.nameError)
            }

            if (registerState.idError != null) {
                edit_register_id.error = getString(registerState.idError)
            }

            if (registerState.passwordError != null) {
                edit_register_password.error = getString(registerState.passwordError)
            }

            if (registerState.passwordCheckError != null) {
                edit_register_password_check.error = getString(registerState.passwordCheckError)
            }

            if (registerState.emailError != null) {
                edit_register_email.error = getString(registerState.emailError)
            }

            if (registerState.codeError != null) {
                edit_register_code.error = getString(registerState.codeError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    progress_register.visibility = View.VISIBLE

                } else
                    progress_register.visibility = View.INVISIBLE
            }
        })

        //// 키보드 숨기기 액션
        edit_register_email.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                UtilKeyboard.hideKeyboard(this)
                return@OnKeyListener true
            }
            false
        })

        btn_register_verification.setOnClickListener {
            viewModel.sendEmail()
            UtilKeyboard.hideKeyboard(this)
        }
        ////
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSuccessRegister() {
        progress_register.visibility = View.INVISIBLE

        mBuilder = AlertDialog.Builder(this)
        mBuilder.setMessage("회원가입에 성공하였습니다.").setCancelable(false)
        mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }

    override fun onDuplicateId() {
        Toast.makeText(this, R.string.message_register_duplicate_id, Toast.LENGTH_SHORT).show()
        edit_register_id.error = getString(R.string.message_register_duplicate_id)
        progress_register.visibility = View.INVISIBLE
    }

    override fun onSuccessSend() {
        runOnUiThread {
            edit_register_email.isEnabled = false
            edit_register_email.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

            mBuilder = AlertDialog.Builder(this)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            progress_register.visibility = View.INVISIBLE
        }
    }

    override fun onDuplicateEmail() {
        runOnUiThread {
            Toast.makeText(this, R.string.message_register_duplicate_email, Toast.LENGTH_SHORT).show()
            progress_register.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        Toast.makeText(this, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
        progress_register.visibility = View.INVISIBLE
    }
}
