package com.example.swimming.ui.profile

data class ProfileFormState(
    val id: String? = null, // 프로필 아이디
    val email: String? = null, // 프로필 이메일
    val onLoading: Int? = null, // 프로그레스 로딩중 표시
    val onError: Int? = null // 에러 발생시
)
