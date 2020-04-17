package com.example.swimming.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.databinding.FragmentNotificationBinding
import com.example.swimming.ui.result.Result
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NotificationFragment : Fragment(), Result, KodeinAware {
    override val kodein by kodein()
    private val factory: ProfileViewModelFactory by instance()
    private lateinit var mBinding: FragmentNotificationBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentNotificationBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        viewModel.recyclerView = mBinding.recyclerNotification
        viewModel.refreshLayout = mBinding.swipeNotifiaction
        viewModel.checkMessage(this, "User", "MessageInfo")

        viewModel.recyclerView = mBinding.recyclerNotification

        return mBinding.root
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailed() {
        TODO("Not yet implemented")
    }

    override fun onError() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}