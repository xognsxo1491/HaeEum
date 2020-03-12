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
import com.example.swimming.utils.UtilShowDialog
import kotlinx.android.synthetic.main.activity_board_write.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FreeBoardWriteActivity : AppCompatActivity(), Result, KodeinAware {
    override val kodein by kodein()

    private val factory: BoardViewModelFactory by instance()
    private val SELECT_PICTURE = 1000

    lateinit var viewModel: BoardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityBoardWriteBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)
        viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(binding.toolbarBoard)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_close_24)

        binding.viewModel = viewModel
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

        viewModel.boardFormState.observe(this@FreeBoardWriteActivity, Observer {
            val writeState = it ?: return@Observer

            if (writeState.titleError != null) {
                edit_board_title.error = getString(writeState.titleError)
            }

            if (writeState.contentsError != null) {
                edit_board_contents.error = getString(writeState.contentsError)
            }

            if (writeState.loading != null) {
                UtilShowDialog(this, getString(writeState.loading)).show()
            }
        })

        fab_board_gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, SELECT_PICTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            viewModel.data = data
            viewModel.setImage()
        }
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
            viewModel.write("FreeBoardInfo")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess() {

        val intent = Intent(this,FreeBoardActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onFailed() {
        Toast.makeText(this, "사진은 최대 5장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }
}
