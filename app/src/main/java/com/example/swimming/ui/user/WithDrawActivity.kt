package com.example.swimming.ui.user

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityWithDrawBinding
import com.example.swimming.etc.utils.UtilKeyboard
import com.example.swimming.ui.result.Result
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class WithDrawActivity : AppCompatActivity(), KodeinAware, Result {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: ActivityWithDrawBinding
    private lateinit var mViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_with_draw)
        mViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        mBinding.viewModel = mViewModel
        mViewModel.result = this

        // 이메일 전송 위한 쓰레드
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setSupportActionBar(mBinding.toolbarWithdraw)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.btnWithdraw.setOnClickListener {
            mBinding.progressWithdraw.visibility = View.VISIBLE
            mViewModel.confirmPassword(mBinding.editWithdraw.text.toString())
        }

        mViewModel.registerFormStatus.observe(this, Observer {
            val status = it ?: return@Observer

            // 비밀번호 오류
            if (status.passwordError != null) {
                UtilKeyboard.hideKeyboard(this)
                onFailed()
                mBinding.editWithdraw.error = getString(R.string.message_change_password)
            }

            // 인증코드 입력 성공
            if (status.success != null) {
                Toast.makeText(this, getString(status.success), Toast.LENGTH_SHORT).show()
                finishAffinity()
            }

            // 인증코드 입력 실패
            if (status.error != null) {
                Toast.makeText(this, getString(status.error), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

    // 비밀번호 입력 성공
    override fun onSuccess() {
        mBinding.progressWithdraw.visibility = View.INVISIBLE
        val dialog = AlertDialog.Builder(this)
        val edit = EditText(this)
        edit.inputType = InputType.TYPE_CLASS_NUMBER
        dialog.setMessage("이메일로 인증코드가 전송되었습니다.\n인증코드를 입력해주세요.").setCancelable(false)
            .setView(edit)
            .setPositiveButton("확인"){_, _ ->
                mViewModel.withdraw(edit.text.toString())

            }.setNegativeButton("취소"){_,_ -> }.show()
    }

    // 비밀번호 입력 실패
    override fun onFailed() {
        mBinding.progressWithdraw.visibility = View.INVISIBLE
        Toast.makeText(this, getString(R.string.message_register_failed), Toast.LENGTH_SHORT).show()
    }

    // 오류 발생
    override fun onError() {
        mBinding.progressWithdraw.visibility = View.INVISIBLE
        Toast.makeText(this, getString(R.string.message_error), Toast.LENGTH_SHORT).show()
    }
}
