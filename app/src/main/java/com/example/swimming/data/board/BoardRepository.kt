package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.example.swimming.utils.UtilBase64Cipher
import java.util.*

class BoardRepository(private val dataSource: BoardDataSource, val context: Context) {

    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")
    private val token = pref.getString("token", "")

    private val time = System.currentTimeMillis().toString()
    private val uuid = (9999999999999 - System.currentTimeMillis()).toString() + UUID.randomUUID().toString()

    fun writeBoard(title: String, contents: String, imgCount: String, path1: String, path2: String) =
        dataSource.writeBoard(token!!, UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, UtilBase64Cipher.encode(time), uuid, UtilBase64Cipher.encode(imgCount), UtilBase64Cipher.encode("0"), UtilBase64Cipher.encode("0"), path1, path2)

    fun writeBoard2(title: String, contents: String, imgCount: String, path1: String, path2: String, store: String, latitude: Double, longitude: Double) =
        dataSource.writeBoard2(token!!, UtilBase64Cipher.encode(title), UtilBase64Cipher.encode(contents), context, UtilBase64Cipher.encode(time), uuid, UtilBase64Cipher.encode(imgCount), UtilBase64Cipher.encode("0"), UtilBase64Cipher.encode("0"), path1, path2, UtilBase64Cipher.encode(store), latitude, longitude)

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
        dataSource.uploadComments(path1, path2, child,(9999999999999 - System.currentTimeMillis()).toString() + UUID.randomUUID().toString(), UtilBase64Cipher.encode(id!!), UtilBase64Cipher.encode(time), UtilBase64Cipher.encode(comments))

    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, child: String) =
        dataSource.loadComments(owner, path1, path2, child)

    fun loadCommentCount(path1: String, path2: String, uuid: String) =
        dataSource.loadCommentCount(path1, path2, uuid)

    fun updateCommentCount(path1: String, path2: String, uuid: String) =
        dataSource.updateCommentCount(path1, path2, uuid)

    fun uploadBoardLike(path1: String, path2: String, uuid1: String) =
        dataSource.uploadBoardLike(path1, path2, uuid1, UtilBase64Cipher.encode(id!!))

    fun loadBoardLike(path1: String, path2: String, uuid: String) =
        dataSource.loadBoardLike(path1, path2, uuid)

    fun deleteBoardLike(path1: String, path2: String, uuid1: String) =
        dataSource.deleteBoardLike(path1, path2, uuid1, UtilBase64Cipher.encode(id!!))

    fun checkBoardLike(path1: String, path2: String, uuid: String) =
        dataSource.checkBoardLike(path1, path2, uuid, UtilBase64Cipher.encode(id!!))

    fun updateBoardLikeCount(path1: String, path2: String, uuid: String) =
        dataSource.updateBoardLikeCountPlus(path1, path2, uuid)

    fun updateBoardLikeCountMinus(path1: String, path2: String, uuid: String) =
        dataSource.updateBoardLikeCountMinus(path1, path2, uuid)

    fun searchKeyword(path1: String, path2: String, keyword: String) =
        dataSource.searchKeyword(path1, path2, keyword)

    fun myBoard(path1: String, path2: String) =
        dataSource.myBoard(path1, path2, id!!)

    fun myComments(path1: String, path2: String, path3: String) =
        dataSource.myComments(path1, path2, path3, UtilBase64Cipher.encode(id!!))

    fun pushToken(title: String, contents: String, token: String, fcm: String, key: String) =
        dataSource.pushToken(title, contents, token, fcm, key)

    fun pushMessage(path1: String, path2: String, uuid1: String, uuid2: String, kind: String, title: String, contents: String) =
        dataSource.pushMessage(path1, path2, UtilBase64Cipher.encode(id!!), uuid1, uuid2, UtilBase64Cipher.encode(kind), UtilBase64Cipher.encode(title),
            UtilBase64Cipher.encode(contents), UtilBase64Cipher.encode(time), UtilBase64Cipher.encode("false"))
}