package com.example.swimming.ui.user


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R

import com.example.swimming.databinding.FragmentChangePasswordBinding
import com.example.swimming.ui.result.Result
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

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

           if (registerState.passwordNowError != null) {
               mBinding.editChangePwNow.error = getString(registerState.passwordNowError)
           }

            if (registerState.passwordError != null) {
                mBinding.editChangePwNew.error = getString(registerState.passwordError)
            }

            if (registerState.passwordCheckError != null) {
                mBinding.editChangePwNewCheck.error = getString(registerState.passwordCheckError)
            }

            if (registerState.success != null) {
                onSuccess()
            }

            if (registerState.error != null) {
                onError()
            }

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
        TODO("Not yet implemented")
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
