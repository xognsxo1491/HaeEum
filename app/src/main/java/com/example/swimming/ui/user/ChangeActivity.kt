package com.example.swimming.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityChangeBinding
import com.example.swimming.ui.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_change.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 개인정보 변경
class ChangeActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: ActivityChangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setSupportActionBar(mBinding.toolbarChange)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.viewModel = viewModel

        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(ChangePasswordFragment(), "비밀번호 변경")
        adapter.addFragment(ChangeEmailFragment(), "이메일 변경")

        viewPager_change.adapter = adapter
        tabLayout_change.setupWithViewPager(viewPager_change)
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
}