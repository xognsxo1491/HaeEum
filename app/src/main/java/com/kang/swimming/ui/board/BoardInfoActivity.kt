package com.kang.swimming.ui.board

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kang.swimming.R
import com.kang.swimming.databinding.ActivityBoardInfoBinding
import com.kang.swimming.etc.utils.UtilDateFormat
import com.kang.swimming.etc.utils.UtilKeyboard
import com.kang.swimming.etc.utils.utilShowDialog
import kotlinx.android.synthetic.main.activity_board_info.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.android.synthetic.main.item_contents.*
import kotlinx.android.synthetic.main.item_contents.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.Exception
import java.util.*

// 게시글 내용
class BoardInfoActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardInfoBinding
    private lateinit var mViewModel: BoardViewModel

    private lateinit var mBuilder: AlertDialog.Builder
    private lateinit var pref: SharedPreferences
    private lateinit var id: String
    private var mLastClickTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_info)
        mViewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(mBinding.toolbarInfo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        pref = getSharedPreferences("Login", Context.MODE_PRIVATE)
        id = pref.getString("Id", "")!!

        val id = intent.getStringExtra("id")
        val kind = intent.getStringExtra("BoardKind")
        val uuid = intent.getStringExtra("uuid")
        val imgCount = intent.getStringExtra("imgCount")
        val token = intent.getStringExtra("token")

        mBinding.viewModel = mViewModel
        mViewModel.recyclerView = mBinding.includeInfo.recycler_Info
        mViewModel.refreshLayout = mBinding.swipeInfo

        mViewModel.card1 = mBinding.includeInfo.card_info1
        mViewModel.card2 = mBinding.includeInfo.card_info2
        mViewModel.card3 = mBinding.includeInfo.card_info3
        mViewModel.card4 = mBinding.includeInfo.card_info4
        mViewModel.card5 = mBinding.includeInfo.card_info5
        mViewModel.imgLike = mBinding.includeInfo.img_favorite
        mViewModel.textView = mBinding.includeInfo.text_board_commentCount
        img_favorite.tag = Integer.valueOf(R.string.unLike)

        mViewModel.cardComment = mBinding.cardInfoComments
        mViewModel.cardCommentComment = mBinding.cardInfoCommentsComments
        mViewModel.textCommentComment = mBinding.textCommentId

        mBinding.includeInfo.text_board_id.text = id
        mBinding.includeInfo. text_board_title.text = intent.getStringExtra("title")
        mBinding.includeInfo. text_board_contents.text = intent.getStringExtra("contents")!!.replace(" ", "\u00A0")
        mBinding.includeInfo. text_board_time.text = UtilDateFormat.formatting(intent.getStringExtra("time")!!.toLong())
        mBinding.includeInfo.text_board_imgCount.text = imgCount
        mBinding.includeInfo. text_board_commentCount.text = intent.getStringExtra("comment")
        mBinding.includeInfo. text_board_like.text = intent.getStringExtra("like")

        val dialog = utilShowDialog(this, "헤엄중..")
        dialog.show()

        mViewModel.checkBoard(kind!!, kind +"Info", uuid!!) // 취소 확인
        mViewModel.loadImage(kind+"/${intent.getStringExtra("uuid")}", imgCount!!) // 이미지 불러오기
        mViewModel.loadComments(this, kind, kind+"Comments", kind+"Info", uuid) // 댓글 불러오기
        mViewModel.checkBoardLike(kind, kind+"Like", uuid) // 좋아요 구독 상태
        mViewModel.loadBoardLike(kind, kind+"Info", uuid) // 좋아요 개수

        when (kind) {
            "FreeBoard" -> mBinding.textInfoTTitle.text = getString(R.string.free_board)
            "InfoBoard" -> mBinding.textInfoTTitle.text = getString(R.string.info_board)
            "Dictionary" -> mBinding.textInfoTTitle.text = getString(R.string.dictionary_board)
        }

        mViewModel.boardFormStatus.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            // 삭제 알림
            if (boardState.check != null) {
                mBuilder = AlertDialog.Builder(this)
                mBuilder.setMessage( boardState.check).setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()
            }

            // 댓글 개수
            if (boardState.setCommentCount != null) {
                mBinding.includeInfo.text_board_commentCount.text = boardState.setCommentCount
            }

            // 좋아요 개수
            if (boardState.setLikeCount != null) {
                mBinding.includeInfo.text_board_like.text = boardState.setLikeCount
            }

            // 좋아요 누름 알림 메시지
            if (boardState.messageLike != null) {
                Toast.makeText(this, boardState.messageLike, Toast.LENGTH_SHORT).show()
            }

            // 에러 발생
            if (boardState.error != null) {
                Toast.makeText(this, boardState.error, Toast.LENGTH_SHORT).show()
            }

            // 삭제시
            if (boardState.delete != null) {
                Toast.makeText(this, getString(R.string.message_delete), Toast.LENGTH_SHORT).show()
                mBinding.includeInfo. text_board_commentCount.text = (Integer.parseInt(text_board_commentCount.text.toString()) - 1).toString()
            }

            // 이미지 있을 시
            try {
                if (boardState.image0 != null) {
                    img_info0.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image0)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info0).waitForLayout()

                    if (imgCount == "1") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image1 != null) {
                    card_info1.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image1)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info1).waitForLayout()
                }

                if (boardState.image2 != null) {
                    card_info2.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info2).waitForLayout()

                    if (imgCount == "2") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image3 != null) {
                    card_info3.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image3)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info3).waitForLayout()

                    if (imgCount == "3") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image4 != null) {
                    card_info4.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image4)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info4).waitForLayout()

                    if (imgCount == "4") {
                        dialog.dismiss()
                    }
                }

                if (boardState.image5 != null) {
                    card_info5.visibility = View.VISIBLE
                    Glide.with(this).load(boardState.image5)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mBinding.includeInfo.img_info5).waitForLayout()

                    if (imgCount == "5") {
                        dialog.dismiss()
                    }
                }
            } catch (e: Exception) {

            }

            // 이미지 없을 시
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
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) { return@setOnClickListener }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()

            if (mBinding.editComments.text.toString().isNotEmpty()) {
                UtilKeyboard.hideKeyboard(this)

                mViewModel.uploadComments(kind, kind+"Comments", uuid)
                mViewModel.updateCommentCountPlus(kind, kind+"Info", uuid)
                mBinding.includeInfo.text_board_commentCount.text = (Integer.parseInt(text_board_commentCount.text.toString()) + 1).toString()

                if (mBinding.includeInfo.text_board_id.text.toString() != id) {
                    if (pref.getBoolean("tab", true)) {
                        mViewModel.pushMessage("User", "MessageInfo", uuid, kind, text_board_title.text.toString(), mBinding.editComments.text.toString())
                    }

                    if (pref.getBoolean("alarm", true)) {
                        mViewModel.pushToken(getString(R.string.message_comments), "댓글: " + edit_comments.text.toString(), token!!, getString(R.string.post_fcm), getString(R.string.authorization))
                    }
                }

                mBinding.editComments.text = null
            }
        }

        // 대댓글 작성
        mBinding.imgCommentsComments.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) { return@setOnClickListener }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()

            if (mBinding.editCommentsComments.text.toString().isNotEmpty()) {
                UtilKeyboard.hideKeyboard(this)

                mViewModel.uploadCommentsComments(kind, kind+"Comments", uuid, mViewModel.commentComment!! + System.currentTimeMillis().toString() + UUID.randomUUID(),"${mBinding.textCommentId.text} ${mBinding.editCommentsComments.text}")
                mViewModel.updateCommentCountPlus(kind, kind+"Info", uuid)
                mBinding.includeInfo.text_board_commentCount.text = (Integer.parseInt(text_board_commentCount.text.toString()) + 1).toString()

                if (mBinding.includeInfo.text_board_id.text.toString() != id) {
                    if (pref.getBoolean("tab", true)) {
                        mViewModel.pushMessage("User", "MessageInfo", uuid, kind, text_board_title.text.toString(), mBinding.editComments.text.toString())
                    }

                    if (pref.getBoolean("alarm", true)) {
                        mViewModel.pushToken(getString(R.string.message_comments), "댓글: " + edit_comments.text.toString(), token!!, getString(R.string.post_fcm), getString(R.string.authorization))
                    }
                }

                mBinding.editCommentsComments.text = null
                mBinding.cardInfoCommentsComments.visibility = View.GONE
                mBinding.cardInfoComments.visibility = View.VISIBLE
            }
        }

        // 좋아요
        mBinding.includeInfo.layout_contents_favorite.setOnClickListener {
            if (mBinding.includeInfo.img_favorite.tag == Integer.valueOf(R.string.unLike)) {
                mBinding.includeInfo.img_favorite.tag = Integer.valueOf(R.string.like)
                mBinding.includeInfo.img_favorite.setImageResource(R.drawable.round_favorite_24)
                mBinding.includeInfo.text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) + 1).toString()

                mViewModel.uploadBoardLike(kind, kind+"Like", uuid)
                mViewModel.updateBoardLikeCountPlus(kind, kind+"Info", uuid)

            } else if (mBinding.includeInfo.img_favorite.tag == Integer.valueOf(R.string.like)){
                mBinding.includeInfo.img_favorite.tag = Integer.valueOf(R.string.unLike)
                mBinding.includeInfo.img_favorite.setImageResource(R.drawable.round_favorite_border_24)
                mBinding.includeInfo. text_board_like.text = (Integer.parseInt(text_board_like.text.toString()) - 1).toString()

                mViewModel.deleteBoardLike(kind, kind+"Like", uuid)
                mViewModel.updateBoardLikeCountMinus(kind, kind+"Info", uuid)
            }
        }
    }

    override fun onBackPressed() {
        if (mBinding.cardInfoCommentsComments.visibility == View.VISIBLE) {
            mBinding.cardInfoCommentsComments.visibility = View.GONE
            mBinding.cardInfoComments.visibility = View.VISIBLE

        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (id == text_board_id.text.toString() || id == "Admin1")
            menuInflater.inflate(R.menu.menu_delete, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            // 삭제
            R.id.menu_delete -> {
                val kind = intent.getStringExtra("BoardKind")
                val uuid = intent.getStringExtra("uuid")
                val imgCount = intent.getStringExtra("imgCount")

                mViewModel.deleteBoard(kind!!, kind + "Info", kind + "Comments", uuid!!, imgCount!!)
                mViewModel.deleteBoardLike(kind, kind + "Like", uuid)

                mBuilder = AlertDialog.Builder(this)
                mBuilder.setMessage("해당 글이 삭제되었습니다.").setCancelable(false)
                mBuilder.setPositiveButton("확인") {_, _ -> finish()}.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}