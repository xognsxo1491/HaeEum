package com.example.swimming.ui.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityFreeBoardBinding
import com.example.swimming.ui.user.UserViewModelFactory
import kotlinx.android.synthetic.main.activity_free_board.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FreeBoardActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityFreeBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_free_board)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

        setSupportActionBar(binding.toolbarFree)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        binding.viewModel = viewModel
        viewModel.recyclerView = binding.recyclerBoard
        viewModel.refreshLayout = binding.swipeFree
        viewModel.load(this, "FreeBoardInfo")

        fab_free.setOnClickListener {
            val intent = Intent(this, FreeBoardWriteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
