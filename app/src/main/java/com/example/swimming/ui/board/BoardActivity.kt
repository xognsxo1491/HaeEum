package com.example.swimming.ui.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardBinding
import kotlinx.android.synthetic.main.activity_board.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 게시판 리스트
class BoardActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()

    private var boardKind: String? = null
    var binding: ActivityBoardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(binding!!.toolbarFree)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding!!.viewModel = viewModel
        viewModel.recyclerView = binding!!.recyclerBoard
        viewModel.refreshLayout = binding!!.swipeFree

        boardKind = intent.getStringExtra("BoardKind")

            when (boardKind) {
            "FreeBoard" -> {
                viewModel.loadBoardList(this, "FreeBoard", "FreeBoardInfo")
            }
        }

        fab_free.setOnClickListener {
            val intent = Intent(this, BoardWriteActivity::class.java)
            intent.putExtra("BoardKind", boardKind)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.unbind()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        if (item.itemId == R.id.menu_search) {
            val intent = Intent(this, BoardSearchActivity::class.java)
            intent.putExtra("BoardKind", boardKind)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }
}
