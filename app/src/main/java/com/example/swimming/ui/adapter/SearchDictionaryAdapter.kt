package com.example.swimming.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList

// 검색
class SearchDictionaryAdapter internal constructor (list: ArrayList<Board>) : RecyclerView.Adapter<SearchDictionaryAdapter.ViewHolder>() {
    private var mData: ArrayList<Board> = list
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_list_dictionary, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Board = mData[position]

        holder.id.text = UtilBase64Cipher.decode(item.id)
        holder.title.text = UtilBase64Cipher.decode(item.title)
        holder.contents.text = UtilBase64Cipher.decode(item.contents).replace(" ", "\u00A0")
        holder.image.text = UtilBase64Cipher.decode(item.imgCount)
        holder.comments.text = UtilBase64Cipher.decode(item.commentCount)
        holder.like.text = UtilBase64Cipher.decode(item.like)

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

        if (holder.image.text.toString() == "0") {
            holder.layout.visibility = View.GONE
            holder.cardView.visibility = View.GONE

        } else
            holder.loadImage(item.uuid, context!!)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.text_board_id)
        val title: TextView = itemView.findViewById(R.id.text_board_title)
        val contents: TextView = itemView.findViewById(R.id.text_board_contents)
        val image: TextView = itemView.findViewById(R.id.text_board_imgCount)
        val comments: TextView = itemView.findViewById(R.id.text_board_commentCount)
        val like: TextView = itemView.findViewById(R.id.text_board_like)
        val layout: LinearLayout = itemView.findViewById(R.id.layout_list_img)
        val thumbnail: ImageView = itemView.findViewById(R.id.img_dictionary)
        val cardView: CardView = itemView.findViewById(R.id.dash_dictionary)

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

        fun loadImage(uuid: String, context: Context) {
            FirebaseStorage.getInstance().getReference("Dictionary/$uuid/1").downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).thumbnail(0.1f).into(thumbnail)
            }
        }
    }
}