package com.example.swimming.singletone

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.swimming.ui.board.DashBoardFragment
import com.example.swimming.ui.board.HomeFragment
import com.example.swimming.ui.board.NotificationFragment
import com.example.swimming.R
import com.example.swimming.data.user.User
import com.example.swimming.utils.UtilBase64Cipher
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.include_main.*

// Singleton
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val SELECT_PICTURE = 1000
    private lateinit var mFilePath: Uri
    private lateinit var mNav: NavigationView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mProfileImage: ImageView

    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        loadFragment(HomeFragment())

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_perm_identity_24)

        val pref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        id = pref.getString("Id","")

        mNav = findViewById(R.id.nav_main_view)
        mNav.setNavigationItemSelectedListener(this)

        mProfileImage = mNav.getHeaderView(0).findViewById(R.id.img_nav)
        mProgressBar = mNav.getHeaderView(0).findViewById(R.id.progress_nav)
        val navId = mNav.getHeaderView(0).findViewById<TextView>(R.id.text_nav_id)
        val navEmail = mNav.getHeaderView(0).findViewById<TextView>(R.id.text_nav_email)
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_main)

        val database = FirebaseDatabase.getInstance()

        // 하단 네비게이션 설정
        bottomNav.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.navigation_home -> {
                    val homeFragment =
                        HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, homeFragment).commit()
                }
                R.id.navigation_dashboard -> {
                    val dashBoardFragment =
                        DashBoardFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, dashBoardFragment).commit()
                }
                R.id.navigation_notifications -> {
                    val notificationBoard =
                        NotificationFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.frame_main, notificationBoard).commit()
                }
            }
            true
        }

        // 프로필 정보 불러오기
        database.getReference("UserInfo").child(UtilBase64Cipher.encode(id!!)).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, R.string.message_error, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)

                navId.text = id
                navEmail.text = UtilBase64Cipher.decode(user!!.email.toString())
            }
        })

        // 프로필 이미지 불러오기
        val storage = FirebaseStorage.getInstance().getReference("Profile/$id").downloadUrl
        storage.addOnSuccessListener{ uri ->
            Glide.with(this).load(uri).into(mProfileImage)
            mProfileImage.background = ShapeDrawable(OvalShape())
            mProfileImage.clipToOutline = true
        }

        // 갤러리 이동
        mProfileImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), SELECT_PICTURE)
        }
    }

    // 사진 선택 Result Action
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mFilePath = data.data!!
                upLoadImage()
                mProgressBar.visibility = View.VISIBLE
            }
        }
    }

    // 이미지 업로드
    private fun upLoadImage() {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("Profile/$id")

        ref.putFile(mFilePath).addOnSuccessListener {
            mProfileImage.setImageURI(mFilePath)
            mProfileImage.background = ShapeDrawable(OvalShape())
            mProfileImage.clipToOutline = true
            mProgressBar.visibility = View.INVISIBLE
        }
    }

    // 로그아웃
    private fun logout() {
        val mPref = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val mEditor = mPref.edit()

        val mBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        mBuilder.setMessage("로그아웃 하시겠습니까?")
        mBuilder.setPositiveButton("확인") {_, _ ->

            finishAffinity()
            mEditor.clear().apply()
            Toast.makeText(this, R.string.message_logout, Toast.LENGTH_SHORT).show()
        }
        mBuilder.setNegativeButton("취소") {_, _ -> }.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_logout -> {
                logout()
            }
        }
        return true
    }

    // 초기화면 설정
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
}
