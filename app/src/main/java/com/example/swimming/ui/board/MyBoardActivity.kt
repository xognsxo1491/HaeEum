package com.example.swimming.ui.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.databinding.ActivityMyBoardBinding
import com.example.swimming.ui.recycler.MyBoardAdapter
import kotlinx.android.synthetic.main.activity_my_board.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MyBoardActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityMyBoardBinding
    lateinit var viewModel: BoardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_board)
        viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(mBinding.toolbarMyBoard)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        viewModel.recyclerView = recycler_myBoard

        when (intent.getStringExtra("Kind")) {
            "Board" -> {
                text_myBoard_title.text  = getString(R.string.myBoard)
                viewModel.myBoard("FreeBoard", "FreeBoardInfo")
                viewModel.myBoard("InfoBoard", "InfoBoardInfo")
            }

            "Comments" -> {
                text_myBoard_title.text  = getString(R.string.myComment)
                viewModel.myComments("FreeBoard", "FreeBoardComments", "FreeBoardInfo")
                viewModel.myComments("InfoBoard", "InfoBoardComments", "InfoBoardInfo")
            }
        }

        val list = ArrayList<Board>()
        viewModel.boardFormState.observe(this, Observer {
            val state = it ?: return@Observer

            if (state.board != null) {
                list.add(state.board)
            }

            viewModel.recyclerView!!.adapter = MyBoardAdapter(list)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
