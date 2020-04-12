package com.example.swimming.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.board.BoardInfoActivity
import com.example.swimming.ui.board.BoardInfoMapActivity
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var id: TextView = itemView.findViewById(R.id.text_board_id)
    private var title: TextView = itemView.findViewById(R.id.text_board_title)
    private var contents: TextView = itemView.findViewById(R.id.text_board_contents)
    private var image: TextView = itemView.findViewById(R.id.text_board_imgCount)
    private var comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
    private var like: TextView = itemView.findViewById(R.id.text_board_like)
    private var cardView: CardView = itemView.findViewById(R.id.dash_dictionary)
    private var layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)
    private var thumbnail: ImageView = itemView.findViewById(R.id.img_dictionary)

    fun setItem(post: Board, context: Context, uuid: String) {
        id.text = UtilBase64Cipher.decode(post.id)
        title.text = UtilBase64Cipher.decode(post.title)
        contents.text = UtilBase64Cipher.decode(post.contents).replace(" ", "\u00A0")
        image.text = UtilBase64Cipher.decode(post.imgCount)
        comments.text = UtilBase64Cipher.decode(post.commentCount)
        like.text = UtilBase64Cipher.decode(post.like)

        if (image.text.toString() == "0") {
            layout.visibility = View.GONE
            cardView.visibility = View.GONE

        } else {
            FirebaseStorage.getInstance().getReference("Dictionary/$uuid/1").downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).thumbnail(0.1f).into(thumbnail)
            }
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
            intent.putExtra("like", UtilBase64Cipher.decode(post.like))
            intent.putExtra("token", post.token)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}