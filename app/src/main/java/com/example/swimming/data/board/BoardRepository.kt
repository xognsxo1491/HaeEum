package com.example.swimming.data.board

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.data.board.BoardDataSource
import com.example.swimming.utils.UtilBase64Cipher

class BoardRepository(private val dataSource: BoardDataSource) {

    fun write(title: String, contents: String, context: Context, path: String) =
        dataSource.write(UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, path)

    fun load(owner: LifecycleOwner, path: String) =
        dataSource.load(owner, path)

    fun loadInfo(id: TextView, title: TextView, contents: TextView, time: TextView, profile: ImageView, path: String, child: String) =
        dataSource.loadInfo(id, title, contents, time, profile, path, child)
}