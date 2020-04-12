package com.example.swimming.ui.board

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.databinding.FragmentDashBoardBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// 게시판 프레그먼트
class DashBoardFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: BoardViewModelFactory by instance()
    private lateinit var mBinding: FragmentDashBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(BoardViewModel::class.java)

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

        mBinding.cardDashReview.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind", "StoreBoard")
            startActivity(intent)
        }

        mBinding.cardDashDictionary.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind", "Dictionary")
            startActivity(intent)
        }

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
