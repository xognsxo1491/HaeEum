package com.example.swimming.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.swimming.databinding.FragmentHomeBinding
import com.example.swimming.ui.board.BoardActivity
import com.example.swimming.ui.board.BoardInfoActivity
import com.example.swimming.ui.map.BoardMapActivity
import com.example.swimming.utils.UtilBase64Cipher
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class HomeFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val factory: ProfileViewModelFactory by instance()
    private lateinit var mBinding: FragmentHomeBinding

    private val uuid1 = "8413482065239a7e0fb86-f1f5-4343-80e5-8b9d3844864c"
    private val uuid2 = "84134116407260c7877c5-0168-4cc5-a228-3a7f3cfabdd5"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        mBinding.viewModel = viewModel

        viewModel.showDictionary1(uuid1)
        viewModel.showDictionary2(uuid2)
        viewModel.showDictionaryImage1(uuid1)
        viewModel.showDictionaryImage2(uuid2)

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

            if (status.title1 != null) {
                mBinding.textFishName1.text = status.title1
                mBinding.progressBar1.visibility = View.GONE
            }

            if (status.title2 != null) {
                mBinding.textFishName2.text = status.title2
                mBinding.progressBar2.visibility = View.GONE
            }

            if (status.content1 != null) {
                mBinding.textFishContent1.text = status.content1
            }

            if (status.content2 != null) {
                mBinding.textFishContent2.text = status.content2
            }

            if (status.image1 != null) {
                Glide.with(context!!).load(status.image1).into(mBinding.imgFishName1)
            }

            if (status.image2 != null) {
                Glide.with(context!!).load(status.image2).into(mBinding.imgFishName2)
            }

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
                    intent.putExtra("token", status.token)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
            }

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
                    intent.putExtra("token", status.token)
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
