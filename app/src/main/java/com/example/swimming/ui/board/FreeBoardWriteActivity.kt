package com.example.swimming.ui.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardWriteBinding
import com.example.swimming.ui.result.Result
import com.example.swimming.ui.user.UserViewModelFactory
import kotlinx.android.synthetic.main.activity_board_write.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FreeBoardWriteActivity : AppCompatActivity(), Result, KodeinAware {
    override val kodein by kodein()

    private val factory: BoardViewModelFactory by instance()
    lateinit var viewModel: BoardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityBoardWriteBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)
        viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(binding.toolbarBoard)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel
        viewModel.result = this

        viewModel.writeFormState.observe(this@FreeBoardWriteActivity, Observer {
            val writeState = it ?: return@Observer

            if (writeState.titleError != null) {
                edit_board_title.error = getString(writeState.titleError)
            }

            if (writeState.contentsError != null) {
                edit_board_contents.error = getString(writeState.contentsError)
            }
        })
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
        Toast.makeText(this, R.string.board_success, Toast.LENGTH_SHORT).show()

        val intent = Intent(this,FreeBoardActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError() {
        Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
    }
}
