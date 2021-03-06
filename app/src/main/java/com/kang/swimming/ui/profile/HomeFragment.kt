package com.kang.swimming.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kang.swimming.databinding.FragmentHomeBinding
import com.kang.swimming.ui.board.BoardActivity
import com.kang.swimming.ui.board.BoardInfoActivity
import com.kang.swimming.ui.map.BoardMapActivity
import com.kang.swimming.etc.utils.UtilBase64Cipher
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HomeFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: ProfileViewModelFactory by instance()
    private lateinit var mBinding: FragmentHomeBinding

    private val uuid1 = "8413151709426f0a7bb95-d850-4c92-b32d-0180c6219101" // 이달의 물고기1
    private val uuid2 = "8413153431221354b8cf6-e8d5-400f-8ed6-a39f02a3c84a" // 이달의 물고기2
    private val uuid3 = "8413150781656eae41706-c424-423f-a929-d4f30ba52bf5" // 이달의 물고기3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        mBinding.viewModel = viewModel

        viewModel.showDictionary1(uuid1)
        viewModel.showDictionary2(uuid2)
        viewModel.showDictionary3(uuid3)

        mBinding.cardFree.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind","FreeBoard")
            startActivity(intent)
        }

        mBinding.cardInfo.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind","InfoBoard")
            startActivity(intent)
        }

        mBinding.cardReview.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind", "StoreBoard")
            startActivity(intent)
        }

        mBinding.cardDictionary.setOnClickListener {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra("BoardKind", "Dictionary")
            startActivity(intent)
        }

        mBinding.cardMap.setOnClickListener {
            val intent = Intent(context, BoardMapActivity::class.java)
            startActivity(intent)
        }

        viewModel.profileFormStatus.observe(viewLifecycleOwner, Observer {
            val status = it ?: return@Observer

            // 이달의 물고기1
            if (status.board1 != null) {
                mBinding.layoutFish1.setOnClickListener {
                    val intent = Intent(context, BoardInfoActivity::class.java)
                    intent.putExtra("BoardKind", "Dictionary")
                    intent.putExtra("uuid", uuid1)
                    intent.putExtra("id", UtilBase64Cipher.decode(status.board1.id))
                    intent.putExtra("title", UtilBase64Cipher.decode(status.board1.title))
                    intent.putExtra("contents", UtilBase64Cipher.decode(status.board1.contents))
                    intent.putExtra("time", UtilBase64Cipher.decode(status.board1.time))
                    intent.putExtra("imgCount", UtilBase64Cipher.decode(status.board1.imgCount))
                    intent.putExtra("comment", UtilBase64Cipher.decode(status.board1.commentCount))
                    intent.putExtra("like", UtilBase64Cipher.decode(status.board1.like))
                    intent.putExtra("token", status.board1.token)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
            }

            // 이달의 물고기2
            if (status.board2 != null) {
                mBinding.layoutFish2.setOnClickListener {
                    val intent = Intent(context, BoardInfoActivity::class.java)
                    intent.putExtra("BoardKind", "Dictionary")
                    intent.putExtra("uuid", uuid2)
                    intent.putExtra("id", UtilBase64Cipher.decode(status.board2.id))
                    intent.putExtra("title", UtilBase64Cipher.decode(status.board2.title))
                    intent.putExtra("contents", UtilBase64Cipher.decode(status.board2.contents))
                    intent.putExtra("time", UtilBase64Cipher.decode(status.board2.time))
                    intent.putExtra("imgCount", UtilBase64Cipher.decode(status.board2.imgCount))
                    intent.putExtra("comment", UtilBase64Cipher.decode(status.board2.commentCount))
                    intent.putExtra("like", UtilBase64Cipher.decode(status.board2.like))
                    intent.putExtra("token", status.board2.token)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
            }

            // 이달의 물고기3
            if (status.board3 != null) {
                mBinding.layoutFish3.setOnClickListener {
                    val intent = Intent(context, BoardInfoActivity::class.java)
                    intent.putExtra("BoardKind", "Dictionary")
                    intent.putExtra("uuid", uuid3)
                    intent.putExtra("id", UtilBase64Cipher.decode(status.board3.id))
                    intent.putExtra("title", UtilBase64Cipher.decode(status.board3.title))
                    intent.putExtra("contents", UtilBase64Cipher.decode(status.board3.contents))
                    intent.putExtra("time", UtilBase64Cipher.decode(status.board3.time))
                    intent.putExtra("imgCount", UtilBase64Cipher.decode(status.board3.imgCount))
                    intent.putExtra("comment", UtilBase64Cipher.decode(status.board3.commentCount))
                    intent.putExtra("like", UtilBase64Cipher.decode(status.board3.like))
                    intent.putExtra("token", status.board3.token)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
            }
        })

        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}
