package com.example.swimming.data.map

import android.content.Context
import com.example.swimming.etc.utils.UtilBase64Cipher

// 지도 관련 저장소
class MapRepository(private val dataSource: MapDataSource, val context: Context) {
    private val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
    private val id = pref.getString("Id", "")

    fun showPosition(path1: String, path2: String) =
        dataSource.showPosition(path1, path2)

    fun boardInfo(path1: String, path2: String, uuid: String) =
        dataSource.boardInfo(path1, path2, uuid)

    fun myLike(path1: String, path2: String, path3: String) =
        dataSource.myLike(path1, path2, path3, UtilBase64Cipher.encode(id!!))
}