package com.kang.swimming.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kang.swimming.R
import com.kang.swimming.data.board.Board
import com.kang.swimming.ui.board.BoardInfoActivity
import com.kang.swimming.ui.board.BoardInfoMapActivity
import com.kang.swimming.etc.utils.UtilBase64Cipher
import com.kang.swimming.etc.utils.UtilTimeFormat

// 게시판 리사이클러뷰 어댑터
class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var id: TextView = itemView.findViewById(R.id.text_board_id)
    private var title: TextView = itemView.findViewById(R.id.text_board_title)
    private var contents: TextView = itemView.findViewById(R.id.text_board_contents)
    private var time: TextView = itemView.findViewById(R.id.text_board_time)
    private var image: TextView = itemView.findViewById(R.id.text_board_imgCount)
    private var comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
    private var like: TextView = itemView.findViewById(R.id.text_board_like)

    fun setItem(post: Board) {
        id.text = UtilBase64Cipher.decode(post.id)
        title.text = UtilBase64Cipher.decode(post.title)
        contents.text = UtilBase64Cipher.decode(post.contents).replace(" ", "\u00A0")
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        image.text = UtilBase64Cipher.decode(post.imgCount)
        comments.text = UtilBase64Cipher.decode(post.commentCount)
        like.text = UtilBase64Cipher.decode(post.like)

        if (image.text.toString() == "0") {
            val layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)
            layout.visibility = View.GONE
        }
    }

    // 일반 게시판
    fun onClick(view: View, context: Context, post: Board, kind: String) {
        view.setOnClickListener {
            val intent = Intent(context, BoardInfoActivity::class.java)
            intent.putExtra("BoardKind", kind)
            intent.putExtra("uuid", post.uuid)
            intent.putExtra("id", UtilBase64Cipher.decode(post.id))
            intent.putExtra("title", UtilBase64Cipher.decode(post.title))
            intent.putExtra("contents", UtilBase64Cipher.decode(post.contents))
            intent.putExtra("time", UtilBase64Cipher.decode(post.time))
            intent.putExtra("imgCount", UtilBase64Cipher.decode(post.imgCount))
            intent.putExtra("comment", UtilBase64Cipher.decode(post.commentCount))
            intent.putExtra("like", UtilBase64Cipher.decode(post.like))
            intent.putExtra("token", post.token)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    // 수족관 게시판
    fun onClick2(view: View, context: Context, post: Board, kind: String) {
        view.setOnClickListener {
            val intent = Intent(context, BoardInfoMapActivity::class.java)
            intent.putExtra("BoardKind", kind)
            intent.putExtra("uuid", post.uuid)
            intent.putExtra("id", UtilBase64Cipher.decode(post.id))
            intent.putExtra("title", UtilBase64Cipher.decode(post.title))
            intent.putExtra("contents", UtilBase64Cipher.decode(post.contents))
            intent.putExtra("time", UtilBase64Cipher.decode(post.time))
            intent.putExtra("imgCount", UtilBase64Cipher.decode(post.imgCount))
            intent.putExtra("comment", UtilBase64Cipher.decode(post.commentCount))
            intent.putExtra("like", UtilBase64Cipher.decode(post.like))
            intent.putExtra("token", post.token)
            intent.putExtra("store", UtilBase64Cipher.decode(post.store))
            intent.putExtra("latitude", post.latitude)
            intent.putExtra("longitude", post.longitude)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}