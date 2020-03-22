package com.example.swimming.ui.board


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.databinding.FragmentDashBoardBinding

// 자유 게시판
class DashBoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentDashBoardBinding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, BoardViewModelFactory(context!!)).get(BoardViewModel::class.java)

        binding.viewModel = viewModel
        binding.cardDashFree.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind","FreeBoard")
            startActivity(intent)
        }

        return binding.root
    }
}
