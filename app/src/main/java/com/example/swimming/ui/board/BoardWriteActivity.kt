package com.example.swimming.ui.board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardWriteBinding
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.utilShowDialog
import kotlinx.android.synthetic.main.activity_board_write.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 게시글 작성
class BoardWriteActivity : AppCompatActivity(), Result, KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardWriteBinding
    lateinit var viewModel: BoardViewModel

    private val code = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)
        viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(mBinding.toolbarBoard)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_close_24)

        mBinding.viewModel = viewModel
        viewModel.result = this

        viewModel.linearLayout = layout_write

        viewModel.img1 = img_board_1
        viewModel.img2 = img_board_2
        viewModel.img3 = img_board_3
        viewModel.img4 = img_board_4
        viewModel.img5 = img_board_5

        viewModel.card1 = card_write_1
        viewModel.card2 = card_write_2
        viewModel.card3 = card_write_3
        viewModel.card4 = card_write_4
        viewModel.card5 = card_write_5

        viewModel.boardFormStatus.observe(this@BoardWriteActivity, Observer {
            val state = it ?: return@Observer

            if (state.titleError != null) {
                mBinding.editBoardTitle.error = getString(state.titleError)
            }

            if (state.contentsError != null) {
                mBinding.editBoardContents.error = getString(state.contentsError)
            }

            if (state.loading != null) {
                utilShowDialog(this, getString(state.loading)).show()
            }
        })

        mBinding.fabBoardGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, code)
        }

        mBinding.editBoardTitle.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            viewModel.data = data
            viewModel.setImage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        if (item.itemId == R.id.menu_write) {

            when (intent.getStringExtra("BoardKind")) {
                "FreeBoard" -> {
                    viewModel.writeBoard("FreeBoard", "FreeBoardInfo")
                    return true
                }

                "InfoBoard" -> {
                    viewModel.writeBoard("InfoBoard", "InfoBoardInfo")
                    return true
                }

                "StoreBoard" -> {
                    viewModel.writeBoard2("StoreBoard", "StoreBoardInfo", intent.getStringExtra("storeName")!!, intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess() {
        when (intent.getStringExtra("BoardKind")) {
            "FreeBoard" -> {
                val intent = Intent(this,BoardActivity::class.java)
                intent.putExtra("BoardKind", "FreeBoard")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }

            "InfoBoard" -> {
                val intent = Intent(this,BoardActivity::class.java)
                intent.putExtra("BoardKind", "InfoBoard")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }

            "StoreBoard" -> {
                val intent = Intent(this,BoardActivity::class.java)
                intent.putExtra("BoardKind", "StoreBoard")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    override fun onFailed() {
        Toast.makeText(this, "사진은 최대 5장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }
}
