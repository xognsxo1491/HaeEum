package com.example.swimming.ui.board

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoBinding
import com.example.swimming.utils.UtilKeyboard
import kotlinx.android.synthetic.main.activity_board_info.*
import kotlinx.android.synthetic.main.item_board.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class BoardInfoActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityBoardInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_info)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(binding.toolbarInfo)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel
        viewModel.recyclerViewComments = binding.recyclerViewInfo
        viewModel.refreshLayoutComments = binding.swipeInfo

        val uuid = intent.getStringExtra("UUID")
        viewModel.downloadComments(this, "FreeBoardComments", uuid!!)
        viewModel.downloadInfo( "FreeBoardInfo", uuid)

        img_comments.setOnClickListener {

            viewModel.uploadComments("FreeBoardComments",uuid, edit_comments.text.toString())
            edit_comments.text = null

            UtilKeyboard.hideKeyboard(this)
        }

        viewModel.boardFormState.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.setId != null) {
                text_board_id.text = boardState.setId
                include_info.visibility = View.VISIBLE
            }

            if (boardState.setTitle != null) {
                text_board_title.text = boardState.setTitle
            }

            if (boardState.setContents != null) {
                text_board_contents.text = boardState.setContents
            }

            if (boardState.setTime != null) {
                text_board_time.text = boardState.setTime
            }

            if (boardState.error != null) {
                include_info.visibility = View.VISIBLE
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
}