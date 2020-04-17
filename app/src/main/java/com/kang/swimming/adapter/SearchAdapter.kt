package com.kang.swimming.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kang.swimming.R
import com.kang.swimming.data.board.Board
import com.kang.swimming.ui.board.BoardInfoActivity
import com.kang.swimming.ui.board.BoardInfoMapActivity
import com.kang.swimming.etc.utils.UtilBase64Cipher
import com.kang.swimming.etc.utils.UtilTimeFormat
import kotlin.collections.ArrayList

// 검색 리사이클러뷰 어댑터
class SearchAdapter internal constructor (list: ArrayList<Board>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var mData: ArrayList<Board> = list
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_list, parent, false)
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

        if (item.latitude == 0.0) {
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
        } else {
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
                UtilBase64Cipher.decode(item.store),
                item.latitude,
                item.longitude
            )
        }

        if (holder.image.text.toString() == "0") {
            holder.layout.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.text_board_id)
        val title: TextView = itemView.findViewById(R.id.text_board_title)
        val contents: TextView = itemView.findViewById(R.id.text_board_contents)
        val time: TextView = itemView.findViewById(R.id.text_board_time)
        val image: TextView = itemView.findViewById(R.id.text_board_imgCount)
        val comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
        val like: TextView = itemView.findViewById(R.id.text_board_like)
        val layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)

        // 일반 게시글 클릭
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

        // 수족관 게시글 클릭
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
    }
}