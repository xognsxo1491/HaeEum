package com.example.swimming.ui.user


import android.app.AlertDialog
import android.os.Bundle
import android.os.StrictMode
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R

import com.example.swimming.databinding.FragmentChangeEmailBinding
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.UtilKeyboard
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ChangeEmailFragment : Fragment(), KodeinAware, Result {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: FragmentChangeEmailBinding

    private lateinit var mBuilder: AlertDialog.Builder
    private var mLastClickTime: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel.email2 = mBinding.editChangeEmailNow
        viewModel.email1 = mBinding.editChangeEmailNew
        viewModel.code = mBinding.editChangeCode
        viewModel.result = this

        mBinding.btnChangeVerification.setOnClickListener {
            viewModel.sendEmailForChange()
            UtilKeyboard.hideKeyboard(activity!!)
        }

        mBinding.btnChangeEmail.setOnClickListener {
            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            viewModel.changeEmail()
        }

        viewModel.registerFormStatus.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            if (registerState.success != null) {
                mBuilder = AlertDialog.Builder(context)
                mBuilder.setMessage("이메일을 변경하였습니다.").setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> activity!!.finishAffinity()}.show()
            }

            if (registerState.codeError != null) {
                onFailed()
                mBinding.editChangeCode.error = getString(registerState.codeError)
            }

            if (registerState.send != null) {
                onSuccess()
                mBinding.progressChangeEmail.visibility = View.INVISIBLE
            }

            if (registerState.emailNowError != null) {
                onFailed()
                mBinding.editChangeEmailNow.error = getString(registerState.emailNowError)
            }

            if (registerState.emailError != null) {
                onFailed()
                mBinding.editChangeEmailNew.error = getString(registerState.emailError)
            }

            if (registerState.error != null) {
                onError()
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    mBinding.progressChangeEmail.visibility = View.VISIBLE

                } else
                    mBinding.progressChangeEmail.visibility = View.INVISIBLE
            }
        })

        return mBinding.root
    }

    override fun onSuccess() {
        activity!!.runOnUiThread {
            mBuilder = AlertDialog.Builder(activity)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            mBinding.progressChangeEmail.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
            mBinding.progressChangeEmail.visibility = View.INVISIBLE
        }
    }

    override fun onError() {
        Toast.makeText(context, getString(R.string.message_error), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
