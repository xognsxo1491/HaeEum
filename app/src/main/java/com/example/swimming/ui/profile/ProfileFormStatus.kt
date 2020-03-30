package com.example.swimming.ui.profile

data class ProfileFormStatus(
    val id: String? = null, // 프로필 아이디
    val email: String? = null, // 프로필 이메일
    val token: String? = null,
    val onLoading: Int? = null, // 프로그레스 로딩중 표시
    val onError: Int? = null,

    val onClick: String? = null,
    var onDelete: String? = null
)
