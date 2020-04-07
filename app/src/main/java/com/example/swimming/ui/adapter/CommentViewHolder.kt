package com.example.swimming.ui.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Comments
import com.example.swimming.ui.board.BoardFormStatus
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val boardForm = MutableLiveData<BoardFormStatus>()

    private var id: TextView = itemView.findViewById(R.id.text_comments_id)
    private var time: TextView = itemView.findViewById(R.id.text_comments_time)
    private var contents: TextView = itemView.findViewById(R.id.text_comments_contents)

    fun setItem(post: Comments) {
        id.text = UtilBase64Cipher.decode(post.id)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        contents.text = UtilBase64Cipher.decode(post.contents)
    }

    fun onClick(view: View, context: Context, post: Comments, id: String) {
        view.setOnClickListener {
            if (id == UtilBase64Cipher.decode(post.id)) {
                val items = arrayOf("삭제하기")
                val builder = AlertDialog.Builder(context)
                builder.setItems(items) { _, w ->
                    when (items[w]) {
                        "삭제하기" -> boardForm.value = BoardFormStatus(delete = "삭제하기")
                    }
                }.show()
            }
        }
    }
}