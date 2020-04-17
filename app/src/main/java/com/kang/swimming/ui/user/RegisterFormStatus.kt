package com.kang.swimming.ui.user

data class RegisterFormStatus(
    val nameError: Int? = null, // 이름 에러
    val idError: Int? = null, // 아이디 에러
    val passwordError: Int? = null, // 비밀번호 에러
    val passwordCheckError: Int? = null, // 비밀번호 체크 에러
    val emailError: Int? = null, // 이메일 에러
    val codeError: Int? = null, // 인증코드 에러

    val isProgressValid: Boolean? = null, // 프로그레스 보이기
    val passwordNowError: Int? = null,
    val emailNowError: Int? = null,

    val send: Int? = null,
    val success: Int? = null,
    val error: Int? = null
)