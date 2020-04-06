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
import com.example.swimming.ui.profile.MainActivity
import com.example.swimming.ui.result.Result
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), Result, KodeinAware {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: ActivityLoginBinding

    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        mBinding.viewModel = viewModel
        viewModel.result = this
        viewModel.id = edit_login_id
        viewModel.password1 = edit_login_password

        mBinding.btnLoginRegister.setOnClickListener {
            val intent = Intent(this, ResisterActivity::class.java)
            startActivity(intent)
        }

        mBinding.btnLoginFind.setOnClickListener {
            val intent = Intent(this, FindActivity::class.java)
            startActivity(intent)
        }

        mBinding.btnLoginLogin.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            viewModel.login()
        }

        viewModel.registerFormStatus.observe(this@LoginActivity, Observer {
            val registerState = it ?: return@Observer

            if (registerState.idError != null) {
                mBinding.editLoginId.error = getString(registerState.idError)
            }

            if (registerState.passwordError != null) {
                mBinding.editLoginPassword.error = getString(registerState.passwordError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    mBinding.progressLogin.visibility = View.VISIBLE

                } else
                    mBinding.progressLogin.visibility = View.INVISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

    override fun onSuccess() {
        mBinding.progressLogin.visibility = View.INVISIBLE

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
        mBinding.progressLogin.visibility = View.INVISIBLE
    }

    override fun onFailed() {
        Toast.makeText(this, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
        mBinding.progressLogin.visibility = View.INVISIBLE
    }
}
