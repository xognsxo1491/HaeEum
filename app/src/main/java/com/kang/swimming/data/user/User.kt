package com.kang.swimming.data.user

// 유저 정보
data class User(
    var name: String? = "", // 이름
    var id: String? = "", // 아이디
    var password: String? = "", // 비밀번호
    var email: String? = "" // 이메일
)