package com.kang.swimming.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.R
import com.kang.swimming.databinding.ActivityFindBinding
import com.kang.swimming.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_find.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 개인정보 찾기
class FindActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: UserViewModelFactory by instance()
    private lateinit var mBinding: ActivityFindBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_find)
        val viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setSupportActionBar(mBinding.toolbarFind)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.viewModel = viewModel

        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(FindIdFragment(), "아이디 찾기")
        adapter.addFragment(FindPasswordFragment(),"비밀번호 찾기")

        mBinding.viewPagerFind.adapter = adapter
        mBinding.tabLayoutFind.setupWithViewPager(viewPager_find)
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
