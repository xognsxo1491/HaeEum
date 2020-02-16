package com.example.swimming.ui.result

interface UserActionResult {
    fun onSuccessRegister() // 가입 성공
    fun onSuccessSend() // 이메일 전송 성공
    fun onDuplicateId() // 아이디 중복
    fun onDuplicateEmail() // 이메일 중복
    fun onError() // 에러 발생
    fun onFailed() // 입력 형식 오류
}