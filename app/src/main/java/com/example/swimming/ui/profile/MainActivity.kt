package com.example.swimming.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
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
import com.bumptech.glide.Glide
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
    private val code = 1000

    lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding.viewModel = viewModel
        viewModel.profileActionResult = this
        viewModel.setProfile()
        viewModel.downloadProfileImage()

        loadFragment(HomeFragment())

        setSupportActionBar(toolbar_main)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_perm_identity_24)

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

        viewModel.profileImage = nav_main_view.getHeaderView(0).findViewById(R.id.img_nav)
        viewModel.profileImage!!.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), code)
        }

        // 프로필 이미지 업로드 UI
        viewModel.profileFormState.observe(this@MainActivity, Observer {
            val userState = it ?: return@Observer

            if (userState.uploadProfileImage != null) {
                viewModel.profileImage!!.setImageURI(userState.uploadProfileImage)
                viewModel.profileImage!!.background = ShapeDrawable(OvalShape())
                viewModel.profileImage!!.clipToOutline = true
            }

            // 프로필 이미지 불러오기 UI
            if (userState.downloadProfileImage != null) {
                Glide.with(this)
                    .load(userState.downloadProfileImage)
                    .fitCenter()
                    .placeholder(R.drawable.round_account_circle_24)
                    .error(R.drawable.round_account_circle_24)
                    .into(viewModel.profileImage!!)

                viewModel.profileImage!!.background = ShapeDrawable(OvalShape())
                viewModel.profileImage!!.clipToOutline = true
            }

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            viewModel.data = data
            viewModel.uploadProfileImage()

            progress = nav_main_view.getHeaderView(0).findViewById(R.id.progress_nav)
            progress!!.visibility = View.VISIBLE
        }
    }

    // Drawer 불러오기
    @SuppressLint("RtlHardcoded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {

            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.openDrawer(Gravity.LEFT)

            } else drawer.closeDrawer(Gravity.LEFT)

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
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT)

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
