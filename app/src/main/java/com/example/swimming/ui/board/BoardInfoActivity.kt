package com.example.swimming.ui.board

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoBinding
import com.example.swimming.utils.UtilShowDialog
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
        val dialog = UtilShowDialog(this, getString(R.string.loading))
        dialog.show()

        setSupportActionBar(binding.toolbarInfo)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel

        val num = intent.getStringExtra("UUID")
        viewModel.downloadInfo( "FreeBoardInfo", num!!)

        viewModel.boardFormState.observe(this@BoardInfoActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.setId != null) {
                text_board_id.text = boardState.setId
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

            if (boardState.setImage != null) {
                Glide.with(this)
                    .load(boardState.setImage)
                    .fitCenter()
                    .placeholder(R.drawable.round_account_circle_24)
                    .error(R.drawable.round_account_circle_24)
                    .into(img_board)

                img_board.background = ShapeDrawable(OvalShape())
                img_board.clipToOutline = true

                include_info.visibility = View.VISIBLE
                dialog.dismiss()
            }

            if (boardState.error != null) {
                include_info.visibility = View.VISIBLE
                dialog.dismiss()
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