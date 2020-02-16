package com.example.swimming.ui.board

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardInfoBinding
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

        val num = intent.getStringExtra("num")
        viewModel.loadInfo(text_board_id, text_board_title, text_board_comment, text_board_time, img_board,"FreeBoardInfo", num!!)

        img_board.background = ShapeDrawable(OvalShape())
        img_board.clipToOutline = true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}