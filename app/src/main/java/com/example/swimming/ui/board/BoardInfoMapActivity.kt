package com.example.swimming.ui.board

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoMapBinding
import com.example.swimming.utils.UtilDateFormat
import com.example.swimming.utils.UtilKeyboard
import com.example.swimming.utils.utilShowDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_board_info.*
import kotlinx.android.synthetic.main.item_board.*
import kotlinx.android.synthetic.main.item_contents.*
import kotlinx.android.synthetic.main.item_contents.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

class BoardInfoMapActivity : AppCompatActivity(), KodeinAware, OnMapReadyCallback {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardInfoMapBinding

    private val key = "AIzaSyC-x0hbrLoMnWp607rYLIMzuKQP7QL0PKs"
    private lateinit var mBuilder: AlertDialog.Builder
    private lateinit var mMap: GoogleMap
    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_info_map)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.viewModel = viewModel

        var bundle: Bundle? = null
        if (savedInstanceState != null) {
            bundle = savedInstanceState.getBundle(key)
        }

        mBinding.mapView.onCreate(bundle)
        mBinding.mapView.getMapAsync(this)

        viewModel.recyclerView = recycler_Info
        viewModel.refreshLayout = swipe_info

        viewModel.card1 = card_info1
        viewModel.card2 = card_info2
        viewModel.card3 = card_info3
        viewModel.card4 = card_info4
        viewModel.card5 = card_info5
        viewModel.imgLike = img_favorite
        img_favorite.tag = Integer.valueOf(R.string.unLike)

        val uuid = intent.getStringExtra("uuid")
        val imgCount = intent.getStringExtra("imgCount")
        val toekn = intent.getStringExtra("token")

        mBinding.includeInfo.text_board_id.text = intent.getStringExtra("id")
        mBinding.includeInfo. text_board_title.text = intent.getStringExtra("title")
        mBinding.includeInfo. text_board_contents.text = intent.getStringExtra("contents")
        mBinding.includeInfo. text_board_time.text = UtilDateFormat.formatting(intent.getStringExtra("time")!!.toLong())
        mBinding.includeInfo.text_board_imgCount.text = imgCount
        mBinding.includeInfo. text_board_commentCount.text = intent.getStringExtra("comment")
        mBinding.includeInfo. text_board_like.text = intent.getStringExtra("like")
        mBinding.slide.setScrollableView(mBinding.scrollView)

        val dialog = utilShowDialog(this, "헤엄중..")
        dialog.show()

        viewModel.checkBoard("StoreBoard", "StoreBoardInfo", uuid!!) // 취소 확인
        viewModel.loadImage("StoreBoard/${intent.getStringExtra("uuid")}", imgCount!!) // 이미지 불러오기
        viewModel.loadComments(this, "StoreBoard", "StoreBoardComments", "StoreBoardInfo", uuid) // 댓글 불러오기
        viewModel.checkBoardLike("StoreBoard", "StoreBoardLike", uuid) // 좋아요 구독 상태
        viewModel.loadBoardLike("StoreBoard", "StoreBoardInfo", uuid) // 좋아요 개수

        viewModel.boardFormStatus.observe(this@BoardInfoMapActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.check != null) {
                mBuilder = AlertDialog.Builder(this)
                mBuilder.setMessage( boardState.check).setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()

            }

            if (boardState.setCommentCount != null) {
                mBinding.includeInfo.text_board_commentCount.text = boardState.setCommentCount
            }

            if (boardState.setLikeCount != null) {
                mBinding.includeInfo.text_board_like.text = boardState.setLikeCount
            }

            if (boardState.messageLike != null) {
                Toast.makeText(this, boardState.messageLike, Toast.LENGTH_SHORT).show()
            }

            if (boardState.error != null) {
                Toast.makeText(this, boardState.error, Toast.LENGTH_SHORT).show()
            }

            try {
                if (boardState.image0 != null) {
                    img_info0.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image0).into(mBinding.includeInfo.img_info0).waitForLayout()

                    if (imgCount == "1") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image1 != null) {
                    card_info1.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image1).into(mBinding.includeInfo.img_info1).waitForLayout()
                }

                if (boardState.image2 != null) {
                    card_info2.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image2).into(mBinding.includeInfo.img_info2).waitForLayout()

                    if (imgCount == "2") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image3 != null) {
                    card_info3.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image3).into(mBinding.includeInfo.img_info3).waitForLayout()

                    if (imgCount == "3") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image4 != null) {
                    card_info4.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image4).into(mBinding.includeInfo.img_info4).waitForLayout()

                    if (imgCount == "4") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image5 != null) {
                    card_info5.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image5).into(mBinding.includeInfo.img_info5).waitForLayout()

                    if (imgCount == "5") {
                        dialog.dismiss()
                    }
                }
            } catch (e: Exception) {

            }

            if (boardState.setImgCount != null) {
                if (boardState.setImgCount == "0") {
                    mBinding.includeInfo.layout_board_img.visibility = View.GONE

                } else {
                    mBinding.includeInfo.text_board_imgCount.text = boardState.setImgCount
                }
            }
        })

        if (imgCount == "0") {
            dialog.dismiss()
        }

        // 댓글 작성
        mBinding.imgComments.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            if (edit_comments.text.toString().isNotEmpty()) {

                viewModel.uploadComments("StoreBoard", "StoreBoardComments", uuid)
                viewModel.updateCommentCount("StoreBoard", "StoreBoardInfo", uuid)
                viewModel.pushMessage("User", "MessageInfo", uuid, "FreeBoard", text_board_title.text.toString(), edit_comments.text.toString())
                mBinding.includeInfo. text_board_commentCount.text = (Integer.parseInt(text_board_commentCount.text.toString()) + 1).toString()

                viewModel.pushToken(getString(R.string.message_comments), "댓글: " + edit_comments.text.toString(), toekn!!, getString(R.string.post_fcm), getString(R.string.authorization))
                mBinding.editComments.text = null
                UtilKeyboard.hideKeyboard(this)
            }
        }

        layout_contents_favorite.setOnClickListener {

            if (mBinding.includeInfo.img_favorite.tag == Integer.valueOf(R.string.unLike)) {
                mBinding.includeInfo.img_favorite.tag = Integer.valueOf(R.string.like)
                mBinding.includeInfo.img_favorite.setImageResource(R.drawable.round_favorite_24)
                mBinding.includeInfo.text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) + 1).toString()

                viewModel.uploadBoardLike("StoreBoard", "StoreBoardLike", uuid)
                viewModel.updateBoardLikeCountPlus("StoreBoard", "StoreBoardInfo", uuid)

            } else if (mBinding.includeInfo.img_favorite.tag == Integer.valueOf(R.string.like)){
                mBinding.includeInfo.img_favorite.tag = Integer.valueOf(R.string.unLike)
                mBinding.includeInfo.img_favorite.setImageResource(R.drawable.round_favorite_border_24)
                mBinding.includeInfo. text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) - 1).toString()

                viewModel.deleteBoardLike("StoreBoard", "StoreBoardLike", uuid)
                viewModel.updateBoardLikeCountMinus("StoreBoard", "StoreBoardInfo", uuid)

            }
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        val option = MarkerOptions()

        val latitude = intent.getDoubleExtra("latitude", 37.52487)
        val longitude = intent.getDoubleExtra("longitude", 126.92723)
        val store = intent.getStringExtra("store")
        option.position(LatLng(latitude, longitude)).title(store)

        mMap.addMarker(option)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(latitude, longitude), 14f))
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        var bundle = outState.getBundle(key)
        if (bundle == null) {
            bundle = Bundle()
            outState.putBundle(key, bundle)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val pref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val id = pref.getString("Id", "")

        if (id == text_board_id.text.toString())
            menuInflater.inflate(R.menu.menu_delete, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                finish()
                return true
            }

            R.id.menu_delete -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()

    }

    override fun onStop() {
        super.onStop()
        mBinding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.onDestroy()
        mBinding.unbind()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }
}
