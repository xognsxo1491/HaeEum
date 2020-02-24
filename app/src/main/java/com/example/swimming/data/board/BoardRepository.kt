package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.data.board.BoardDataSource
import com.example.swimming.utils.UtilBase64Cipher
import java.util.*

class BoardRepository(private val dataSource: BoardDataSource) {

    fun write(title: String, contents: String, context: Context, path: String, num: String, count: String) =
        dataSource.write(UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, path, num, count)

    fun load(owner: LifecycleOwner, path: String) =
        dataSource.load(owner, path)

    fun loadInfo(path: String, child: String) =
        dataSource.loadInfo(path, child)

    fun loadProfileImage(id: String) =
        dataSource.loadProfileImage(UtilBase64Cipher.decode(id))

    fun uploadImage(path1: String, uuid: String, count: String, data: Intent?) =
        dataSource.uploadImage(path1, uuid, count, data)
}