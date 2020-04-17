package com.example.swimming.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityAlarmBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AlarmActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: ProfileViewModelFactory by instance()
    private lateinit var mBinding: ActivityAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_alarm)
        mBinding.viewModel = viewModel

        setSupportActionBar(mBinding.toolbarAlarm)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        viewModel.switch = mBinding.switchAlarm
        viewModel.switch2 = mBinding.switchTab
        viewModel.loadStatusAlarm()
        viewModel.loadStatusTab()

        mBinding.switchAlarm.setOnClickListener {
            viewModel.checkAlarm()
        }

        mBinding.switchTab.setOnClickListener {
            viewModel.checkTab()
        }
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
}
