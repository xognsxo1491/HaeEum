package com.example.swimming.ui.board

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoBinding
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.UtilBase64Cipher
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
class BoardInfoActivity : AppCompatActivity(), KodeinAware, Result {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityBoardInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_info)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)
        val dialog = utilShowDialog(this, "헤엄중..")

        dialog.show()

        setSupportActionBar(binding.toolbarInfo)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel
        viewModel.comment = text_board_commentCount
        viewModel.recyclerView = recyclerInfo
        viewModel.refreshLayout = swipe_info

        viewModel.img0 = img_info0
        viewModel.img1 = img_info1
        viewModel.img2 = img_info2
        viewModel.img3 = img_info3
        viewModel.img4 = img_info4
        viewModel.img5 = img_info5

        viewModel.card1 = card_info1
        viewModel.card2 = card_info2
        viewModel.card3 = card_info3
        viewModel.card4 = card_info4
        viewModel.card5 = card_info5

        val uuid = intent.getStringExtra("uuid")
        val imgCount = intent.getStringExtra("imgCount")

        when (intent.getStringExtra("BoardKind")) {
            "FreeBoard" -> {
                viewModel.loadComments(this, "FreeBoard", "FreeBoardComments", uuid!!)
                viewModel.infoBoard( "FreeBoard", "FreeBoardInfo", uuid)
                viewModel.loadImage("FreeBoard/$uuid", imgCount!!)
                viewModel.loadCommentCount("FreeBoard", "FreeBoardInfo", uuid)
            }
        }

        img_comments.setOnClickListener {

            when (intent.getStringExtra("BoardKind")) {
                "FreeBoard" -> {
                    viewModel.loadCommentCount("FreeBoard", "FreeBoardInfo", uuid!!)
                    viewModel.uploadComments("FreeBoard", "FreeBoardComments", uuid, edit_comments.text.toString())

                    Handler().postDelayed({

                        viewModel.updateCommentCount("FreeBoard", "FreeBoardInfo", uuid, UtilBase64Cipher.encode((text_board_commentCount.text.toString().toInt()+1).toString()))
                        viewModel.loadCommentCount("FreeBoard", "FreeBoardInfo", uuid)
                    }, 2000)
                }
            }

            edit_comments.text = null
            UtilKeyboard.hideKeyboard(this)
        }

        viewModel.boardFormState.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.setId != null) {
                text_board_id.text = boardState.setId
            }

            if (boardState.setTitle != null) {
                text_board_title.text = boardState.setTitle
            }

            if (boardState.setTime != null) {
                text_board_time.text = boardState.setTime
            }

            if (boardState.setContents != null) {
                try {
                    text_board_contents.text = boardState.setContents
                    dialog.dismiss()
                    include_info.visibility = View.VISIBLE

                } catch (e: Exception) {

                }
            }

            if (boardState.setImgCount != null) {
                if (boardState.setImgCount == "0") {
                    layout_board_img.visibility = View.GONE

                } else {
                    text_board_imgCount.text = boardState.setImgCount
                }
            }

            if (boardState.error != null) {
                onError()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess() {
        TODO("Not yet implemented")
    }

    override fun onFailed() {
        TODO("Not yet implemented")
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }
}