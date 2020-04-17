package com.kang.swimming.ui.user

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.R
import com.kang.swimming.ui.result.UserActionResult
import com.kang.swimming.databinding.ActivityResisterBinding
import com.kang.swimming.etc.utils.UtilKeyboard
import kotlinx.android.synthetic.main.activity_resister.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 회원가입
class ResisterActivity : AppCompatActivity(), UserActionResult, KodeinAware {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: ActivityResisterBinding

    private lateinit var mBuilder: AlertDialog.Builder
    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이메일 전송을 위한 쓰레드 정책
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_resister)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setSupportActionBar(mBinding.toolbarRegister)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.viewModel = viewModel
        viewModel.userActionResult = this
        viewModel.name = edit_register_name
        viewModel.id = edit_register_id
        viewModel.password1 = edit_register_password
        viewModel.passwordCheck = edit_register_password_check
        viewModel.email1 = edit_register_email
        viewModel.code = edit_register_code

        viewModel.registerFormStatus.observe(this@ResisterActivity, Observer {
            val registerState = it ?: return@Observer

            // 이름
            if (registerState.nameError != null) {
                mBinding.editRegisterName.error = getString(registerState.nameError)
            }

            // 아이디
            if (registerState.idError != null) {
                mBinding.editRegisterId.error = getString(registerState.idError)
            }

            // 비밀번호
            if (registerState.passwordError != null) {
                mBinding.editRegisterPassword.error = getString(registerState.passwordError)
            }

            // 비밀번호 확인
            if (registerState.passwordCheckError != null) {
                mBinding.editRegisterPasswordCheck.error = getString(registerState.passwordCheckError)
            }

            // 이메일
            if (registerState.emailError != null) {
                mBinding.editRegisterEmail.error = getString(registerState.emailError)
            }

            // 인증 코드
            if (registerState.codeError != null) {
                mBinding.editRegisterCode.error = getString(registerState.codeError)
            }

            // 프로그레스바 표시
            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    mBinding.progressRegister.visibility = View.VISIBLE

                } else
                    mBinding.progressRegister.visibility = View.INVISIBLE
            }
        })

        // 키보드 숨기기 액션
        mBinding.editRegisterEmail.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                UtilKeyboard.hideKeyboard(this)
                return@OnKeyListener true
            }
            false
        })

        mBinding.btnRegisterVerification.setOnClickListener {
            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) { return@setOnClickListener }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()

            viewModel.sendEmail()
            UtilKeyboard.hideKeyboard(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // 가입 성공
    override fun onSuccessRegister() {
        progress_register.visibility = View.INVISIBLE

        mBuilder = AlertDialog.Builder(this)
        mBuilder.setMessage("회원가입에 성공하였습니다.").setCancelable(false)
        mBuilder.setPositiveButton("확인") {_, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }.show()
    }

    // 에러 발생
    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }

    // 아이디 중복
    override fun onDuplicateId() {
        Toast.makeText(this, R.string.message_register_duplicate_id, Toast.LENGTH_SHORT).show()
        mBinding.editRegisterId.error = getString(R.string.message_register_duplicate_id)
        progress_register.visibility = View.INVISIBLE
    }

    // 이메일 중복
    override fun onDuplicateEmail() {
        runOnUiThread {
            Toast.makeText(this, R.string.message_register_duplicate_email, Toast.LENGTH_SHORT).show()
            mBinding.progressRegister.visibility = View.INVISIBLE
        }
    }

    //  이메일 전송
    override fun onSuccessSend() {
        runOnUiThread {
            mBinding.editRegisterEmail.isEnabled = false
            mBinding.editRegisterEmail.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))

            mBuilder = AlertDialog.Builder(this)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            mBinding.progressRegister.visibility = View.INVISIBLE
        }
    }

    // 실패
    override fun onFailed() {
        Toast.makeText(this, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
        mBinding.progressRegister.visibility = View.INVISIBLE
    }
}
