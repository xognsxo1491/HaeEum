package com.example.swimming.ui.board

import android.os.Bundle
import android.os.SystemClock
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
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.UtilKeyboard
import com.example.swimming.utils.utilShowDialog
import kotlinx.android.synthetic.main.activity_board_info.*
import kotlinx.android.synthetic.main.item_board.*
import kotlinx.android.synthetic.main.item_contents.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*

// 게시글 내용
class BoardInfoActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()

    private val factory: BoardViewModelFactory by instance()
    private var mLastClickTime: Int = 0

    var binding: ActivityBoardInfoBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_info)

        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)
        val date = Date(intent.getStringExtra("time")!!.toLong())
        val format = SimpleDateFormat("MM/dd HH:mm", Locale.KOREA)

        val dialog = utilShowDialog(this, "헤엄중..")
        dialog.show()

        setSupportActionBar(binding!!.toolbarInfo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding!!.viewModel = viewModel
        viewModel.recyclerView = recyclerInfo
        viewModel.refreshLayout = swipe_info

        viewModel.card1 = card_info1
        viewModel.card2 = card_info2
        viewModel.card3 = card_info3
        viewModel.card4 = card_info4
        viewModel.card5 = card_info5

        val uuid = intent.getStringExtra("uuid")
        val imgCount = intent.getStringExtra("imgCount")

        text_board_id.text = intent.getStringExtra("id")
        text_board_title.text = intent.getStringExtra("title")
        text_board_contents.text = intent.getStringExtra("contents")
        text_board_time.text = intent.getStringExtra("time")
        text_board_time.text = format.format(date)
        text_board_imgCount.text = imgCount
        text_board_commentCount.text = intent.getStringExtra("comment")

        when (intent.getStringExtra("BoardKind")) {
            "FreeBoard" -> {
                viewModel.loadComments(this, "FreeBoard", "FreeBoardComments", "FreeBoardInfo", uuid!!)
                viewModel.loadImage("FreeBoard/${intent.getStringExtra("uuid")}", imgCount!!)
            }
        }

        if (imgCount == "0") {
            dialog.dismiss()
        }

        viewModel.boardFormState.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.setCommentCount != null) {
                text_board_commentCount.text = boardState.setCommentCount
            }

            if (boardState.error != null) {
                Toast.makeText(this, boardState.error, Toast.LENGTH_SHORT).show()
            }

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

            if (boardState.setImgCount != null) {
                if (boardState.setImgCount == "0") {
                    layout_board_img.visibility = View.GONE

                } else {
                    text_board_imgCount.text = boardState.setImgCount
                }
            }
        })

        img_comments.setOnClickListener {

            // 중복터치 방지
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime().toInt()
            //

            if (edit_comments.text.toString().isNotEmpty()) {
                when (intent.getStringExtra("BoardKind")) {

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
            img_favorite.setImageResource(R.drawable.round_favorite_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.unbind()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}