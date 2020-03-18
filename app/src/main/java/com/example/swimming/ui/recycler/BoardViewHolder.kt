package com.example.swimming.ui.recycler

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.board.BoardInfoActivity
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat

class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var id: TextView = itemView.findViewById(R.id.text_board_id)
    private var title: TextView = itemView.findViewById(R.id.text_board_title)
    private var contents: TextView = itemView.findViewById(R.id.text_board_contents)
    private var time: TextView = itemView.findViewById(R.id.text_board_time)
    private var image: TextView = itemView.findViewById(R.id.text_board_imgCount)
    private var comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
    private var uuid: String? = null

    fun setItem(post: Board) {
        id.text = UtilBase64Cipher.decode(post.id)
        title.text = UtilBase64Cipher.decode(post.title)
        contents.text = UtilBase64Cipher.decode(post.contents)
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        image.text = UtilBase64Cipher.decode(post.imgCount)
        comments.text = UtilBase64Cipher.decode(post.commentCount)
        uuid = post.uuid

        if (image.text.toString() == "0") {
            val layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)
            layout.visibility = View.GONE
        }
    }

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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}