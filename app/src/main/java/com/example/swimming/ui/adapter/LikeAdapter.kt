package com.example.swimming.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.ui.map.BoardMapActivity
import com.example.swimming.utils.UtilBase64Cipher
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

// 검색
class LikeAdapter internal constructor (list: ArrayList<Board>, map: GoogleMap) : RecyclerView.Adapter<LikeAdapter.ViewHolder>() {
    private var mData: ArrayList<Board> = list
    private var map: GoogleMap? = map
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_list_like, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Board = mData[position]

        val geoCoder = Geocoder(context, Locale.KOREA)
        val address = geoCoder.getFromLocation(item.latitude, item.longitude, 10)

        val add = address[0].getAddressLine(0)

        holder.title.text = UtilBase64Cipher.decode(item.store)
        holder.position.text = add
        holder.onClick(map!!, item.latitude, item.longitude)
    }

    override fun getItemCount(): Int {
        return if (mData.size > 5)
            5
        else
            mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.text_like_text)
        val position: TextView = itemView.findViewById(R.id.text_like_position)

        fun onClick(map: GoogleMap, latitude: Double, longitude: Double) {
            itemView.setOnClickListener {
                val position = LatLng(latitude, longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14f))
            }
        }
    }
}