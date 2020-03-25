package com.example.swimming.ui.board


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.databinding.FragmentDashBoardBinding

// 자유 게시판
class DashBoardFragment : Fragment() {
    private lateinit var mBinding: FragmentDashBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, BoardViewModelFactory(context!!)).get(BoardViewModel::class.java)

        mBinding.viewModel = viewModel
        mBinding.cardDashFree.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind","FreeBoard")
            startActivity(intent)
        }

        mBinding.cardDashInfo.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind", "InfoBoard")
            startActivity(intent)
        }

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
