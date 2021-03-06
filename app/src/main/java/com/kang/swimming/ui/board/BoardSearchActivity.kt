package com.kang.swimming.ui.board

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.R
import com.kang.swimming.data.board.Board
import com.kang.swimming.databinding.ActivityBoardSearchBinding
import com.kang.swimming.adapter.SearchAdapter
import com.kang.swimming.adapter.SearchDictionaryAdapter
import com.kang.swimming.etc.utils.UtilKeyboard
import kotlinx.android.synthetic.main.activity_board_search.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 게시글 검색창
class BoardSearchActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)
        viewModel.recyclerView = recycler_Search

        val list = ArrayList<Board>()

        mBinding.editSearch.requestFocus()
        mBinding.editSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                if (mBinding.editSearch.text.toString().length >= 2) {

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

                        "StoreBoard" -> {
                            list.clear()
                            viewModel.searchKeyword("StoreBoard", "StoreBoardInfo", edit_search.text.toString())
                            UtilKeyboard.hideKeyboard(this)
                        }

                        "Dictionary" -> {
                            list.clear()
                            viewModel.searchKeyword("Dictionary", "DictionaryInfo", edit_search.text.toString())
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

        // 뒤로 가기
        mBinding.layoutSearch.setOnClickListener { finish() }

        viewModel.boardFormStatus.observe(this@BoardSearchActivity, Observer {
            val boardState = it ?: return@Observer

            if (boardState.board != null) {
                list.add(boardState.board)
                list.sortBy { board -> board.uuid }
            }

            if (intent.getStringExtra("BoardKind") == "Dictionary")
                viewModel.recyclerView!!.adapter = SearchDictionaryAdapter(list)

            else
                viewModel.recyclerView!!.adapter = SearchAdapter(list)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
