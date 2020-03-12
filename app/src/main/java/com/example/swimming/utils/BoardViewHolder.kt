package com.example.swimming.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.board.BoardInfoActivity

class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var id: TextView = itemView.findViewById(R.id.text_board_id)
    var title: TextView = itemView.findViewById(R.id.text_board_title)
    var contents: TextView = itemView.findViewById(R.id.text_board_contents)
    var time: TextView = itemView.findViewById(R.id.text_board_time)
    var uuid: String? = null

    fun setItem(post: Board) {
        id.text = UtilBase64Cipher.decode(post.id)
        title.text = UtilBase64Cipher.decode(post.title)
        contents.text = UtilBase64Cipher.decode(post.contents)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        uuid = post.uuid
    }

    fun onClick(view: View, context: Context) {
        view.setOnClickListener {
            val intent = Intent(context, BoardInfoActivity::class.java)
            intent.putExtra("UUID", uuid)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}