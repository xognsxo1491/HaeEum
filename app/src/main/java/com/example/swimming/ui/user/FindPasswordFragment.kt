package com.example.swimming.ui.user


import android.app.AlertDialog
import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.example.swimming.R
import com.example.swimming.databinding.FragmentFindPasswordBinding
import com.example.swimming.ui.result.Result
import kotlinx.android.synthetic.main.fragment_find_id.*
import kotlinx.android.synthetic.main.fragment_find_password.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class FindPasswordFragment : Fragment(), Result, KodeinAware{
    override val kodein by kodein()

    private val factory: UserViewModelFactory by instance()
    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentFindPasswordBinding = FragmentFindPasswordBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.result = this
        viewModel.name = binding.editFindPwName
        viewModel.id = binding.editFindPwId
        viewModel.email = binding.editFindPwEmail

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel.registerFormState.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            if (registerState.nameError != null) {
                edit_find_pw_name.error = getString(registerState.nameError)
            }

            if (registerState.idError != null) {
                edit_find_pw_id.error = getString(registerState.idError)
            }

            if (registerState.emailError != null) {
                edit_find_pw_email.error = getString(registerState.emailError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    progress_find_password.visibility = View.VISIBLE

                } else
                    progress_find_password.visibility = View.INVISIBLE
            }
        })

        return binding.root
    }

    override fun onSuccess() {
        activity!!.runOnUiThread {
            mBuilder = AlertDialog.Builder(activity)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            progress_find_password.visibility = View.INVISIBLE
        }
    }

    override fun onError() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_error, Toast.LENGTH_SHORT).show()
            progress_find_password.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
            progress_find_password.visibility = View.INVISIBLE
        }
    }
}
