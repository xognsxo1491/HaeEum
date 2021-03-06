package com.kang.swimming.ui.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.R
import com.kang.swimming.data.board.Board
import com.kang.swimming.databinding.ActivityMyBoardBinding
import com.kang.swimming.adapter.MyBoardAdapter
import kotlinx.android.synthetic.main.activity_my_board.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 나의 게시글
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
                mBinding.textMyBoardTitle.text  = getString(R.string.myBoard)
                viewModel.myBoard("FreeBoard", "FreeBoardInfo")
                viewModel.myBoard("InfoBoard", "InfoBoardInfo")
                viewModel.myBoard("StoreBoard", "StoreBoardInfo")
                viewModel.myBoard("Dictionary", "DictionaryInfo")
            }

            "Comments" -> {
                mBinding.textMyBoardTitle.text  = getString(R.string.myComment)
                viewModel.myComments("FreeBoard", "FreeBoardComments", "FreeBoardInfo")
                viewModel.myComments( "InfoBoard", "InfoBoardComments", "InfoBoardInfo")
                viewModel.myComments("StoreBoard", "StoreBoardComments", "StoreBoardInfo")
                viewModel.myComments("Dictionary",  "DictionaryComments", "DictionaryInfo")
            }

            "Like" -> {
                mBinding.textMyBoardTitle.text = getString(R.string.myLike)
                viewModel.myLike("FreeBoard", "FreeBoardLike", "FreeBoardInfo")
                viewModel.myLike("InfoBoard", "InfoBoardLike", "InfoBoardInfo")
                viewModel.myLike("StoreBoard", "StoreBoardLike", "StoreBoardInfo")
                viewModel.myLike("Dictionary", "DictionaryLike", "DictionaryInfo")
            }
        }

        val list = ArrayList<Board>()
        viewModel.boardFormStatus.observe(this, Observer {
            val state = it ?: return@Observer

            if (state.board != null) {
                list.add(state.board)
                list.sortBy { board -> board.uuid }
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
