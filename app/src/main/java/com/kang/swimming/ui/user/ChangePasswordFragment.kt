package com.kang.swimming.ui.user

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.R

import com.kang.swimming.databinding.FragmentChangePasswordBinding
import com.kang.swimming.ui.result.Result
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// 비밀번호 변경
class ChangePasswordFragment : Fragment(), KodeinAware, Result {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: FragmentChangePasswordBinding

    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        mBinding.viewModel = viewModel

        viewModel.password2 = mBinding.editChangePwNow
        viewModel.password1 = mBinding.editChangePwNew
        viewModel.passwordCheck = mBinding.editChangePwNewCheck
        viewModel.result = this

        mBinding.btnChangePassword.setOnClickListener {
            viewModel.changePassword()
        }

        viewModel.registerFormStatus.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            // 현재 비밀번호
           if (registerState.passwordNowError != null) {
               mBinding.editChangePwNow.error = getString(registerState.passwordNowError)
           }

            // 새로운 비밀번호
            if (registerState.passwordError != null) {
                mBinding.editChangePwNew.error = getString(registerState.passwordError)
            }

            // 새로운 비밀번호 확인
            if (registerState.passwordCheckError != null) {
                mBinding.editChangePwNewCheck.error = getString(registerState.passwordCheckError)
            }

            // 성공
            if (registerState.success != null) {
                onSuccess()
            }

            // 에러 발생
            if (registerState.error != null) {
                onError()
            }

            // 프로그레스 표시
            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    mBinding.progressChange.visibility = View.VISIBLE

                } else
                    mBinding.progressChange.visibility = View.INVISIBLE
            }
        })

        return mBinding.root
    }

    override fun onSuccess() {
        activity!!.runOnUiThread {
            mBuilder = AlertDialog.Builder(context)
            mBuilder.setMessage(getString(R.string.success_change_password)).setCancelable(false)
            mBuilder.setPositiveButton("확인") { _, _ -> activity!!.finishAffinity() }.show()
            mBinding.progressChange.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        // To do
    }

    override fun onError() {
        activity!!.runOnUiThread {
            Toast.makeText(context, getString(R.string.message_error), Toast.LENGTH_SHORT).show()
            mBinding.progressChange.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
