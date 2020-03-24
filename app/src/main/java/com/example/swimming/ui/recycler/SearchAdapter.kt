package com.example.swimming.ui.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat
import kotlin.collections.ArrayList

class SearchAdapter internal constructor (list: ArrayList<Board>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private var mData: ArrayList<Board> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Board = mData[position]

        holder.id.text = UtilBase64Cipher.decode(item.id)
        holder.title.text = UtilBase64Cipher.decode(item.title)
        holder.contents.text = UtilBase64Cipher.decode(item.contents)
        holder.time.text = UtilTimeFormat.formatting(UtilBase64Cipher.decode((item.time)).toLong())
        holder.image.text = UtilBase64Cipher.decode(item.imgCount)
        holder.comments.text = UtilBase64Cipher.decode(item.commentCount)
        holder.like.text = UtilBase64Cipher.decode(item.like)

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

    }
}