package com.example.swimming.ui.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.profile.Message
import com.example.swimming.ui.board.MyBoardActivity
import com.example.swimming.ui.profile.ProfileFormStatus
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val profileForm = MutableLiveData<ProfileFormStatus>()

    private var kind: TextView = itemView.findViewById(R.id.text_notification_kind)
    private var title: TextView = itemView.findViewById(R.id.text_notification_title)
    private var contents: TextView = itemView.findViewById(R.id.text_notification_contents)
    private var time: TextView = itemView.findViewById(R.id.text_notification_time)
    private var layout: ConstraintLayout = itemView.findViewById(R.id.layout_message_delete)
    private var uuid: String? = null

    @SuppressLint("SetTextI18n")
    fun setItem(post: Message) {
        title.text = "게시글: ${UtilBase64Cipher.decode(post.title)}"
        time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong())))
        uuid = post.uuid

        when (UtilBase64Cipher.decode(post.kind)) {
            "FreeBoard" -> {
                kind.text = "자유 게시판"
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }

            "InfoBoard" -> {
                kind.text = "정보 게시판"
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }
        }

        if (UtilBase64Cipher.decode(post.status) == "true") {
            kind.setTextColor(Color.parseColor("#7E8188"))
            title.setTextColor(Color.parseColor("#7E8188"))
            contents.setTextColor(Color.parseColor("#7E8188"))
            time.setTextColor(Color.parseColor("#7E8188"))

            }
    }

    fun onClick(view: View, context: Context) {
        view.setOnClickListener {
            profileForm.value = ProfileFormStatus(onClick = "true")
            kind.setTextColor(Color.parseColor("#7E8188"))
            title.setTextColor(Color.parseColor("#7E8188"))
            contents.setTextColor(Color.parseColor("#7E8188"))

            val intent = Intent(context, MyBoardActivity::class.java)
            intent.putExtra("Kind", "Board")
            intent.putExtra("message", uuid)
            context.startActivity(intent)
        }

        layout.setOnClickListener {
            profileForm.value = ProfileFormStatus(onDelete = "true")
        }
    }
}