package com.example.swimming.ui.profile

import android.net.Uri
import com.example.swimming.data.board.Board

data class ProfileFormStatus(
    val id: String? = null, // 프로필 아이디
    val email: String? = null, // 프로필 이메일
    val token: String? = null,
    val onLoading: Int? = null, // 프로그레스 로딩중 표시
    val onError: Int? = null,

    val board1: Board? = null,
    val board2: Board? = null,

    val title1: String? = null,
    val title2: String? = null,

    val content1: String? = null,
    val content2: String? = null,

    val image1: Uri? = null,
    val image2: Uri? = null,

    val onClick: String? = null,
    var onDelete: String? = null
)
