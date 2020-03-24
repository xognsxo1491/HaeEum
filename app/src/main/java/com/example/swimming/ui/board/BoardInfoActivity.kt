package com.example.swimming.ui.board

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoBinding
import com.example.swimming.utils.UtilDateFormat
import com.example.swimming.utils.UtilKeyboard
import com.example.swimming.utils.utilShowDialog
import kotlinx.android.synthetic.main.activity_board_info.*
import kotlinx.android.synthetic.main.item_board.*
import kotlinx.android.synthetic.main.item_contents.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception

// 게시글 내용
class BoardInfoActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardInfoBinding

    private lateinit var mBuilder: AlertDialog.Builder
    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_info)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(mBinding.toolbarInfo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        mBinding.viewModel = viewModel
        viewModel.recyclerView = recycler_Info
        viewModel.refreshLayout = swipe_info

        viewModel.card1 = card_info1
        viewModel.card2 = card_info2
        viewModel.card3 = card_info3
        viewModel.card4 = card_info4
        viewModel.card5 = card_info5
        viewModel.imgLike = img_favorite
        img_favorite.tag = Integer.valueOf(R.string.unLike)

        val kind = intent.getStringExtra("BoardKind")
        val uuid = intent.getStringExtra("uuid")
        val imgCount = intent.getStringExtra("imgCount")

        text_board_id.text = intent.getStringExtra("id")
        text_board_title.text = intent.getStringExtra("title")
        text_board_contents.text = intent.getStringExtra("contents")
        text_board_time.text = UtilDateFormat.formatting(intent.getStringExtra("time")!!.toLong())
        text_board_imgCount.text = imgCount
        text_board_commentCount.text = intent.getStringExtra("comment")
        text_board_like.text = intent.getStringExtra("like")

        val dialog = utilShowDialog(this, "헤엄중..")
        dialog.show()

        when (kind) {
            "FreeBoard" -> {
                text_info_tTitle.text = getString(R.string.free_board)
                viewModel.checkBoard("FreeBoard", "FreeBoardInfo", uuid!!) // 취소 확인
                viewModel.loadImage("FreeBoard/${intent.getStringExtra("uuid")}", imgCount!!) // 이미지 불러오기
                viewModel.loadComments(this, "FreeBoard", "FreeBoardComments", "FreeBoardInfo", uuid) // 댓글 불러오기
                viewModel.checkBoardLike("FreeBoard", "FreeBoardLike", uuid) // 좋아요 구독 상태
                viewModel.loadBoardLike("FreeBoard", "FreeBoardInfo", uuid) // 좋아요 개수
            }
        }

        viewModel.boardFormState.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.check != null) {
                mBuilder = AlertDialog.Builder(this)
                mBuilder.setMessage( boardState.check).setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()

            }

            if (boardState.setCommentCount != null) {
                text_board_commentCount.text = boardState.setCommentCount
            }

            if (boardState.setLikeCount != null) {
                text_board_like.text = boardState.setLikeCount
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
                    Glide.with(this).load(boardState.image0).into(img_info0).waitForLayout()

                    if (imgCount == "1") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image1 != null) {
                    card_info1.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image1).into(img_info1).waitForLayout()
                }

                if (boardState.image2 != null) {
                    card_info2.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image2).into(img_info2).waitForLayout()

                    if (imgCount == "2") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image3 != null) {
                    card_info3.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image3).into(img_info3).waitForLayout()

                    if (imgCount == "3") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image4 != null) {
                    card_info4.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image4).into(img_info4).waitForLayout()

                    if (imgCount == "4") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image5 != null) {
                    card_info5.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image5).into(img_info5).waitForLayout()

                    if (imgCount == "5") {
                        dialog.dismiss()
                    }
                }
            } catch (e: Exception) {

            }

            if (boardState.setImgCount != null) {
                if (boardState.setImgCount == "0") {
                    layout_board_img.visibility = View.GONE

                } else {
                    text_board_imgCount.text = boardState.setImgCount
                }
            }
        })

        if (imgCount == "0") {
            dialog.dismiss()
        }

        // 댓글 작성
        img_comments.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            if (edit_comments.text.toString().isNotEmpty()) {
                when (kind) {
                    "FreeBoard" -> {
                        viewModel.uploadComments("FreeBoard", "FreeBoardComments", uuid!!)
                        viewModel.updateCommentCount("FreeBoard", "FreeBoardInfo", uuid)
                        text_board_commentCount.text = (Integer.parseInt(text_board_commentCount.text.toString()) + 1).toString()
                    }
                }

                edit_comments.text = null
                UtilKeyboard.hideKeyboard(this)
            }
        }

        layout_contents_favorite.setOnClickListener {

            if (img_favorite.tag == Integer.valueOf(R.string.unLike)) {
                img_favorite.tag = Integer.valueOf(R.string.like)
                img_favorite.setImageResource(R.drawable.round_favorite_24)
                text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) + 1).toString()

                when (kind) {
                    "FreeBoard" -> {
                        viewModel.uploadBoardLike("FreeBoard", "FreeBoardLike", uuid!!)
                        viewModel.updateBoardLikeCountPlus("FreeBoard", "FreeBoardInfo", uuid)
                    }
                }

            } else if (img_favorite.tag == Integer.valueOf(R.string.like)){
                img_favorite.tag = Integer.valueOf(R.string.unLike)
                img_favorite.setImageResource(R.drawable.round_favorite_border_24)
                text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) - 1).toString()

                when (kind) {
                    "FreeBoard" -> {
                        viewModel.deleteBoardLike("FreeBoard", "FreeBoardLike", uuid!!)
                        viewModel.updateBoardLikeCountMinus("FreeBoard", "FreeBoardInfo", uuid)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
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
                val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)
                val kind = intent.getStringExtra("BoardKind")
                val uuid = intent.getStringExtra("uuid")
                val imgCount = intent.getStringExtra("imgCount")

                viewModel.deleteBoard(kind!!, kind + "Info", kind + "Comments", uuid!!, imgCount!!)
                viewModel.deleteBoardLike(kind, kind + "Like", uuid)

                mBuilder = AlertDialog.Builder(this)
                mBuilder.setMessage("해당 글이 삭제되었습니다.").setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}