package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.utils.UtilBase64Cipher
import java.util.*

class BoardRepository(private val dataSource: BoardDataSource, val context: Context) {

    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")

    private val time = System.currentTimeMillis().toString()
    private val uuid = System.currentTimeMillis().toString() + UUID.randomUUID().toString()

    fun writeBoard(title: String, contents: String, imgCount: String, commentCount: String, path1: String, path2: String) =
        dataSource.writeBoard(UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, uuid, UtilBase64Cipher.encode(imgCount), UtilBase64Cipher.encode(commentCount), path1, path2)

    fun deleteBoard(path1: String, path2: String, path3: String, uuid: String, count: String) =
        dataSource.deleteBoard(path1, path2, path3, uuid, count)

    fun checkBoard(path1: String, path2: String, uuid: String) =
        dataSource.checkBoard(path1, path2, uuid)

    fun loadBoardList(owner: LifecycleOwner, path1: String, path2: String) =
        dataSource.loadBoardList(owner, path1, path2)

    fun uploadImage(path: String, count: String, data: Intent?) =
        dataSource.uploadImage(path, uuid, count, data)

    fun loadImage(path: String, count: String) =
        dataSource.loadImage(path, count)

    fun uploadComments(path1: String, path2: String, child: String, comments: String) =
        dataSource.uploadComments(path1, path2, child,System.currentTimeMillis().toString() + UUID.randomUUID().toString(), UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(time), UtilBase64Cipher.encode(comments))

    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, child: String) =
        dataSource.loadComments(owner, path1, path2, child)

    fun loadCommentCount(path1: String, path2: String, uuid: String) =
        dataSource.loadCommentCount(path1, path2, uuid)

    fun updateCommentCount(path1: String, path2: String, uuid: String) =
        dataSource.updateCommentCount(path1, path2, uuid)

    fun searchKeyword(path1: String, path2: String, keyword: String) =
        dataSource.searchKeyword(path1, path2, keyword)
}