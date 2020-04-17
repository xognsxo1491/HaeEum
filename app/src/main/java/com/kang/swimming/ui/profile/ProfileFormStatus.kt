package com.kang.swimming.ui.profile

import com.kang.swimming.data.board.Board

data class ProfileFormStatus(
    val id: String? = null, // 프로필 아이디
    val email: String? = null, // 프로필 이메일
    val token: String? = null,
    val uuid: String? = null,
    val onLoading: Int? = null, // 프로그레스 로딩중 표시
    val onError: Int? = null,

    val board1: Board? = null,
    val board2: Board? = null,
    val board3: Board? = null
)
