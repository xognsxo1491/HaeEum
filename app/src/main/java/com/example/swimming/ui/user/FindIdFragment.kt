package com.example.swimming.ui.user


import android.app.AlertDialog
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.swimming.R
import com.example.swimming.databinding.FragmentFindIdBinding
import com.example.swimming.ui.result.Result
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FindIdFragment : Fragment(), Result, KodeinAware {
    override val kodein by kodein()

    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: FragmentFindIdBinding
    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentFindIdBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        mBinding.viewModel = viewModel
        viewModel.result = this
        viewModel.name = mBinding.editFindIdName
        viewModel.email1 = mBinding.editFindIdEmail

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel.registerFormStatus.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            if (registerState.nameError != null) {
                mBinding.editFindIdName.error = getString(registerState.nameError)
            }

            if (registerState.emailError != null) {
                mBinding.editFindIdEmail.error = getString(registerState.emailError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    mBinding.progressFindId.visibility = View.VISIBLE

                } else
                    mBinding.progressFindId.visibility = View.INVISIBLE
            }
        })

        return mBinding.root
    }

    override fun onSuccess() {
        activity!!.runOnUiThread {
            mBuilder = AlertDialog.Builder(activity)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            mBinding.progressFindId.visibility = View.INVISIBLE
        }
    }

    override fun onError() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_error, Toast.LENGTH_SHORT).show()
            mBinding.progressFindId.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
            mBinding.progressFindId.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
