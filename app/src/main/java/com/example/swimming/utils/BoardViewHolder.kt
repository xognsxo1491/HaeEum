package com.example.swimming.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.board.BoardInfoActivity

class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var id: TextView = itemView.findViewById(R.id.text_board_id)
    private var title: TextView = itemView.findViewById(R.id.text_board_title)
    private var contents: TextView = itemView.findViewById(R.id.text_board_contents)
    private var time: TextView = itemView.findViewById(R.id.text_board_time)
    private var image: TextView = itemView.findViewById(R.id.text_board_imgCount)
    private var comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
    private var uuid: String? = null
    private var imgCount: String? = null

    fun setItem(post: Board) {
        id.text = UtilBase64Cipher.decode(post.id)
        title.text = UtilBase64Cipher.decode(post.title)
        contents.text = UtilBase64Cipher.decode(post.contents)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        image.text = UtilBase64Cipher.decode(post.imgCount)
        comments.text = UtilBase64Cipher.decode(post.commentCount)
        uuid = post.uuid
        imgCount = post.imgCount

        if (imgCount!! == UtilBase64Cipher.encode("0")) {
            val layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)
            layout.visibility = View.GONE
        }
    }

    fun onClick(view: View, context: Context, kind: String) {
        view.setOnClickListener {
            val intent = Intent(context, BoardInfoActivity::class.java)
            intent.putExtra("uuid", uuid)
            intent.putExtra("imgCount", imgCount)
            intent.putExtra("BoardKind", kind)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}