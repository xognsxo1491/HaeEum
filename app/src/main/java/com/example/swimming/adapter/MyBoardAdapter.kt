package com.example.swimming.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.board.BoardInfoActivity
import com.example.swimming.ui.board.BoardInfoMapActivity
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.example.swimming.etc.utils.UtilTimeFormat

// 나의 게시글 리사이클러뷰 어댑터
class MyBoardAdapter internal constructor (list: ArrayList<Board>) : RecyclerView.Adapter<MyBoardAdapter.ViewHolder>() {
    private var mData: ArrayList<Board> = list
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_mylist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Board = mData[position]

        holder.id.text = UtilBase64Cipher.decode(item.id)
        holder.title.text = UtilBase64Cipher.decode(item.title)
        holder.contents.text = UtilBase64Cipher.decode(item.contents).replace(" ", "\u00A0")
        holder.time.text = UtilTimeFormat.formatting(UtilBase64Cipher.decode((item.time)).toLong())
        holder.image.text = UtilBase64Cipher.decode(item.imgCount)
        holder.comments.text = UtilBase64Cipher.decode(item.commentCount)
        holder.like.text = UtilBase64Cipher.decode(item.like)

        when (UtilBase64Cipher.decode(item.kind)) {
            "FreeBoard" -> {
                holder.kind.text = "자유 게시판"
                holder.onClick(
                    holder.itemView, context!!,
                    UtilBase64Cipher.decode(item.kind),
                    item.uuid,
                    holder.id.text.toString(),
                    holder.title.text.toString(),
                    holder.contents.text.toString(),
                    UtilBase64Cipher.decode(item.time),
                    holder.image.text.toString(),
                    holder.comments.text.toString(),
                    holder.like.text.toString(),
                    item.token
                )
            }

            "InfoBoard" -> {
                holder.kind.text = "정보 게시판"
                holder.onClick(
                    holder.itemView, context!!,
                    UtilBase64Cipher.decode(item.kind),
                    item.uuid,
                    holder.id.text.toString(),
                    holder.title.text.toString(),
                    holder.contents.text.toString(),
                    UtilBase64Cipher.decode(item.time),
                    holder.image.text.toString(),
                    holder.comments.text.toString(),
                    holder.like.text.toString(),
                    item.token
                )
            }

            "Dictionary" -> {
                holder.kind.text = "물고기 백과사전"
                holder.onClick(
                    holder.itemView, context!!,
                    UtilBase64Cipher.decode(item.kind),
                    item.uuid,
                    holder.id.text.toString(),
                    holder.title.text.toString(),
                    holder.contents.text.toString(),
                    UtilBase64Cipher.decode(item.time),
                    holder.image.text.toString(),
                    holder.comments.text.toString(),
                    holder.like.text.toString(),
                    item.token
                )
            }

            "StoreBoard" -> {
                holder.kind.text = "수족관 게시판"
                holder.onClick2(
                    holder.itemView, context!!,
                    UtilBase64Cipher.decode(item.kind),
                    item.uuid,
                    holder.id.text.toString(),
                    holder.title.text.toString(),
                    holder.contents.text.toString(),
                    UtilBase64Cipher.decode(item.time),
                    holder.image.text.toString(),
                    holder.comments.text.toString(),
                    holder.like.text.toString(),
                    item.token,
                    UtilBase64Cipher.decode( item.store),
                    item.latitude,
                    item.longitude
                )
            }
        }

        holder.check(
            context!!,
            UtilBase64Cipher.decode(item.kind),
            item.uuid,
            holder.id.text.toString(),
            holder.title.text.toString(),
            holder.contents.text.toString(),
            UtilBase64Cipher.decode(item.time),
            holder.image.text.toString(),
            holder.comments.text.toString(),
            holder.like.text.toString(),
            item.token,
            UtilBase64Cipher.decode(item.store),
            item.latitude,
            item.longitude
        )

        if (holder.image.text.toString() == "0") {
            holder.layout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mIntent: Intent? = null

        val id: TextView = itemView.findViewById(R.id.text_myBoard_id)
        val title: TextView = itemView.findViewById(R.id.text_myBoard_title)
        val contents: TextView = itemView.findViewById(R.id.text_myBoard_contents)
        val time: TextView = itemView.findViewById(R.id.text_myBoard_time)
        val image: TextView = itemView.findViewById(R.id.text_myBoard_imgCount)
        val comments: TextView = itemView.findViewById(R.id.text_myBoard_commentCount)
        val like: TextView = itemView.findViewById(R.id.text_myBoard_like)
        val kind: TextView = itemView.findViewById(R.id.text_myBoard_kind)
        val layout: LinearLayout = itemView.findViewById(R.id.layout_myList_img)

        // 자유 게시판, 정보 게시판
        fun onClick(itemView: View, context: Context, kind: String, uuid: String, id: String, title: String, contents: String, time: String, imgCount: String, commentCount: String, like: String, token: String) {
            itemView.setOnClickListener {
                val intent = Intent(context, BoardInfoActivity::class.java)
                intent.putExtra("BoardKind", kind)
                intent.putExtra("uuid", uuid)
                intent.putExtra("id", id)
                intent.putExtra("title", title)
                intent.putExtra("contents", contents)
                intent.putExtra("time", time)
                intent.putExtra("imgCount", imgCount)
                intent.putExtra("comment", commentCount)
                intent.putExtra("like", like)
                intent.putExtra("token", token)
                context.startActivity(intent)
            }
        }

        // 수족관 게시판
        fun onClick2(itemView: View, context: Context, kind: String, uuid: String, id: String, title: String, contents: String, time: String, imgCount: String, commentCount: String, like: String, token: String, store: String, latitude: Double, longitude: Double) {
            itemView.setOnClickListener {
                val intent = Intent(context, BoardInfoMapActivity::class.java)
                intent.putExtra("BoardKind", kind)
                intent.putExtra("uuid", uuid)
                intent.putExtra("id", id)
                intent.putExtra("title", title)
                intent.putExtra("contents", contents)
                intent.putExtra("time", time)
                intent.putExtra("imgCount", imgCount)
                intent.putExtra("comment", commentCount)
                intent.putExtra("like", like)
                intent.putExtra("token", token)
                intent.putExtra("store", store)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                context.startActivity(intent)
            }
        }

        fun check(context: Context, kind: String, uuid: String, id: String, title: String, contents: String, time: String, imgCount: String, commentCount: String, like: String, token: String, store: String, latitude: Double, longitude: Double) {
            mIntent = (context as Activity).intent

            // 자유 게시판, 정보 게시판
            if (mIntent!!.getStringExtra("message") == uuid && (kind == "FreeBoard" || kind == "InfoBoard" || kind == "Dictionary")) {
                val intent = Intent(context, BoardInfoActivity::class.java)
                intent.putExtra("BoardKind", kind)
                intent.putExtra("uuid", uuid)
                intent.putExtra("id", id)
                intent.putExtra("title", title)
                intent.putExtra("contents", contents)
                intent.putExtra("time", time)
                intent.putExtra("imgCount", imgCount)
                intent.putExtra("comment", commentCount)
                intent.putExtra("like", like)
                intent.putExtra("token", token)
                context.startActivity(intent)
            }

            // 수족관 게시판
            else if (mIntent!!.getStringExtra("message") == uuid && kind == "StoreBoard") {
                val intent = Intent(context, BoardInfoMapActivity::class.java)
                intent.putExtra("BoardKind", kind)
                intent.putExtra("uuid", uuid)
                intent.putExtra("id", id)
                intent.putExtra("title", title)
                intent.putExtra("contents", contents)
                intent.putExtra("time", time)
                intent.putExtra("imgCount", imgCount)
                intent.putExtra("comment", commentCount)
                intent.putExtra("like", like)
                intent.putExtra("token", token)
                intent.putExtra("store", store)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                context.startActivity(intent)
            }
        }
    }
}