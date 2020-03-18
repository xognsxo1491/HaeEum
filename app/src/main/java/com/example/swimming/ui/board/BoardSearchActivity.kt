package com.example.swimming.ui.board

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardSearchBinding
import kotlinx.android.synthetic.main.activity_board_search.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class BoardSearchActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()

    var binding: ActivityBoardSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        binding!!.viewModel = viewModel

        layout_search.setOnClickListener {
            finish()
        }


        edit_search.requestFocus()
        edit_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                if (edit_search.text.toString().length >= 2) {

                    when (intent.getStringExtra("BoardKind")) {
                        "FreeBoard" -> {
                            viewModel.searchKeyword("FreeBoard", "FreeBoardInfo", edit_search.text.toString())
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

    }

    override fun onDestroy() {
        super.onDestroy()
        binding!!.unbind()
    }
}
