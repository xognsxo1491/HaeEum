package com.example.swimming.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.profile.Message
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat

// 알림 메시지 리사이클러뷰 어댑터
class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var kind: TextView = itemView.findViewById(R.id.text_notification_kind)
    private var contents: TextView = itemView.findViewById(R.id.text_notification_contents)
    private var time: TextView = itemView.findViewById(R.id.text_notification_time)
    private var status: TextView = itemView.findViewById(R.id.text_notification_status)
    private var uuid: String? = null

    @SuppressLint("SetTextI18n")
    fun setItem(post: Message) {
        time.text = UtilTimeFormat.formatting((UtilBase64Cipher.decode(post.time).toLong()))
        status.text = UtilBase64Cipher.decode(post.status)
        uuid = post.uuid

        when (UtilBase64Cipher.decode(post.kind)) {
            "FreeBoard" -> {
                kind.text = "'자유 게시판' 게시글에 댓글이 달렸습니다."
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }

            "InfoBoard" -> {
                kind.text = "'정보 게시판' 게시글에 댓글이 달렸습니다."
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }

            "StoreBoard" -> {
                kind.text = "'수족관 게시판' 게시글에 댓글이 달렸습니다."
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }

            "Dictionary" -> {
                kind.text = "'물고기 백과사전' 게시글에 댓글이 달렸습니다."
                kind.setTextColor(Color.parseColor("#ED5A81"))
                contents.text = "댓글: ${UtilBase64Cipher.decode(post.contents)}"
            }
        }
    }
}