package com.example.swimming.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityMainBinding
import com.example.swimming.ui.board.*
import com.example.swimming.ui.result.ProfileActionResult
import com.example.swimming.utils.UtilBase64Cipher
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// Singleton
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, KodeinAware, ProfileActionResult {
    override val kodein by kodein()

    private val factory: ProfileViewModelFactory by instance()
    private var progress: ProgressBar? = null

    lateinit var viewModel: ProfileViewModel
    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding!!.viewModel = viewModel
        viewModel.profileActionResult = this
        viewModel.progressBar = nav_main_view.getHeaderView(0).findViewById(R.id.progress_nav)
        viewModel.setProfile()

        loadFragment(HomeFragment())

        setSupportActionBar(toolbar_main)

        nav_main_view.setNavigationItemSelectedListener(this)

        // 하단 네비게이션 클릭 리스너
        nav_main.setOnNavigationItemSelectedListener {p0 ->
            when (p0.itemId) {
                R.id.navigation_home -> {
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, homeFragment).commit()
                }
                R.id.navigation_dashboard -> {
                    val dashBoardFragment = DashBoardFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, dashBoardFragment).commit()
                }
                R.id.navigation_notifications -> {
                    val notificationBoard = NotificationFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, notificationBoard).commit()
                }
            }
            true
        }

        // 프로필 이미지 업로드 UI
        viewModel.profileFormState.observe(this@MainActivity, Observer {
            val userState = it ?: return@Observer

            if (userState.onError != null) {
                Toast.makeText(this, getString(userState.onError), Toast.LENGTH_SHORT).show()
            }

            if (userState.id != null) {
                val id = nav_main_view.getHeaderView(0).findViewById<TextView>(R.id.text_nav_id)
                id.text = userState.id
            }

            if (userState.email != null) {
                val email = nav_main_view.getHeaderView(0).findViewById<TextView>(R.id.text_nav_email)
                email.text = UtilBase64Cipher.decode(userState.email)
            }
        })

        viewModel.profileFormState.observe(this@MainActivity, Observer {
            val userState = it ?: return@Observer

            if (userState.onError != null) {
                Toast.makeText(this, userState.onError, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.unbind()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    // Drawer 불러오기
    @SuppressLint("RtlHardcoded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile) {

            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.openDrawer(Gravity.RIGHT)

            } else drawer.closeDrawer(Gravity.RIGHT)

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Drawer 아이템 클릭 리스너
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        when(item.itemId) {
            R.id.nav_logout -> {
                val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                mBuilder.setMessage("로그아웃 하시겠습니까?")
                mBuilder.setPositiveButton("확인") {_, _ ->

                    viewModel.logout()
                    finishAffinity()
                    onLogout()
                }
                mBuilder.setNegativeButton("취소") {_, _ -> }.show()
            }
        }
        return true
    }

    // 프래그먼트 초기화면 설정
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // 백버튼 클릭 시
    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT)

        } else {
            moveTaskToBack(true)
            finishAffinity()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    override fun onLogout() {
        Toast.makeText(this, R.string.message_logout, Toast.LENGTH_SHORT).show()
    }

    override fun onLoad() {
        progress!!.visibility = View.INVISIBLE
    }
}
