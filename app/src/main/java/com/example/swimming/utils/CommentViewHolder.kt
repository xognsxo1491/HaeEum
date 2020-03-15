package com.example.swimming.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.data.board.Comments
import com.example.swimming.ui.board.BoardInfoActivity

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var id: TextView = itemView.findViewById(R.id.text_comments_id)
    private var time: TextView = itemView.findViewById(R.id.text_comments_time)
    private var contents: TextView = itemView.findViewById(R.id.text_comments_contents)

    fun setItem(post: Comments) {
        id.text = UtilBase64Cipher.decode(post.id)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        contents.text = UtilBase64Cipher.decode(post.contents)
    }

    fun onClick(view: View, context: Context) {
        view.setOnClickListener {

        }
    }
}