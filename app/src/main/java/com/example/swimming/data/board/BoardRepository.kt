package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.data.board.BoardDataSource
import com.example.swimming.utils.UtilBase64Cipher
import java.util.*

class BoardRepository(private val dataSource: BoardDataSource, val context: Context) {

    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")

    private val time = System.currentTimeMillis().toString()
    private val uuid = System.currentTimeMillis().toString() + UUID.randomUUID().toString()

    fun write(title: String, contents: String, context: Context, path: String, count: String) =
        dataSource.write(UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, path, uuid, count)

    fun downloadList(owner: LifecycleOwner, path: String) =
        dataSource.downloadList(owner, path)

    fun downloadInfo(path: String, child: String) =
        dataSource.downloadInfo(path, child)

    fun uploadImage(path: String, count: String, data: Intent?) =
        dataSource.uploadImage(path, uuid, count, data)

    fun uploadComments(path: String, child: String, contents: String) =
        dataSource.uploadComments(path, child,System.currentTimeMillis().toString() + UUID.randomUUID().toString(), UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(time), UtilBase64Cipher.encode(contents))

    fun downloadComments(owner: LifecycleOwner, path: String, child: String) =
        dataSource.downloadComments(owner, path, child)
}