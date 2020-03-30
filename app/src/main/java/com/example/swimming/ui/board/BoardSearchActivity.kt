package com.example.swimming.ui.board

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.databinding.ActivityBoardSearchBinding
import com.example.swimming.ui.recycler.SearchAdapter
import com.example.swimming.utils.UtilKeyboard
import kotlinx.android.synthetic.main.activity_board_search.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class BoardSearchActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)

        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)
        val list = ArrayList<Board>()

        viewModel.recyclerView = recycler_Search

        edit_search.requestFocus()
        edit_search.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                if (edit_search.text.toString().length >= 2) {

                    when (intent.getStringExtra("BoardKind")) {
                        "FreeBoard" -> {
                            list.clear()
                            viewModel.searchKeyword("FreeBoard", "FreeBoardInfo", edit_search.text.toString())
                            UtilKeyboard.hideKeyboard(this)
                        }

                        "InfoBoard" -> {
                            list.clear()
                            viewModel.searchKeyword("InfoBoard", "InfoBoardInfo", edit_search.text.toString())
                            UtilKeyboard.hideKeyboard(this)
                        }
                    }
                }

                else
                    Toast.makeText(this, R.string.searchError, Toast.LENGTH_SHORT).show()
                true

            } else {
                false
            }
        }

        layout_search.setOnClickListener {
            finish()
        }

        viewModel.boardFormStatus.observe(this@BoardSearchActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.board != null) {
                list.add(boardState.board)
            }

            viewModel.recyclerView!!.adapter =
                SearchAdapter(list)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
