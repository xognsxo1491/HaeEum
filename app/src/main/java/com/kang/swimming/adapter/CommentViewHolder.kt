package com.kang.swimming.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.kang.swimming.R
import com.kang.swimming.data.board.Comments
import com.kang.swimming.etc.utils.UtilBase64Cipher
import com.kang.swimming.etc.utils.UtilTimeFormat

// 댓글 리사이클러뷰 어댑터
class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var id: TextView = itemView.findViewById(R.id.text_comments_id)
    private var time: TextView = itemView.findViewById(R.id.text_comments_time)
    private var contents: TextView = itemView.findViewById(R.id.text_comments_contents)
    private var layout: ConstraintLayout = itemView.findViewById(R.id.layout_comments_blank)

    fun setItem(post: Comments) {
        id.text = UtilBase64Cipher.decode(post.id)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        contents.text = UtilBase64Cipher.decode(post.contents).replace(" ", "\u00A0")

        if (UtilBase64Cipher.decode(post.kind) == "true") {
            layout.visibility = View.VISIBLE
        }
    }
}