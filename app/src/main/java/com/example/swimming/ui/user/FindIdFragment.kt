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
import kotlinx.android.synthetic.main.fragment_find_id.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class FindIdFragment : Fragment(), Result, KodeinAware {
    override val kodein by kodein()

    private val factory: UserViewModelFactory by instance()
    private lateinit var mBuilder: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentFindIdBinding = FragmentFindIdBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.result = this

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel.registerFormState.observe(viewLifecycleOwner, Observer {
            val registerState = it ?: return@Observer

            if (registerState.nameError != null) {
                edit_find_id_name.error = getString(registerState.nameError)
            }

            if (registerState.emailError != null) {
                edit_find_id_email.error = getString(registerState.emailError)
            }

            if (registerState.isProgressValid != null) {
                if (registerState.isProgressValid == true) {
                    progress_find_id.visibility = View.VISIBLE

                } else
                    progress_find_id.visibility = View.INVISIBLE
            }
        })

        return binding.root
    }

    override fun onSuccess() {
        activity!!.runOnUiThread {
            mBuilder = AlertDialog.Builder(activity)
            mBuilder.setMessage(getString(R.string.message_register_send_email))
            mBuilder.setPositiveButton("확인") {_, _ -> }.show()
            progress_find_id.visibility = View.INVISIBLE
        }
    }

    override fun onError() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_error, Toast.LENGTH_SHORT).show()
            progress_find_id.visibility = View.INVISIBLE
        }
    }

    override fun onFailed() {
        activity!!.runOnUiThread {
            Toast.makeText(view!!.context, R.string.message_register_failed, Toast.LENGTH_SHORT).show()
            progress_find_id.visibility = View.INVISIBLE
        }
    }
}
