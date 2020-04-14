package com.example.swimming.data.profile

// 알림 메시지 내용
data class Message (
    var key: String = "", // 알림 UUID
    var uuid: String = "", // 게시글 UUID
    var kind: String = "", // 게시글 종류
    var title: String = "", // 제목
    var contents: String = "", // 내용
    var time: String = "", // 시간
    var status: String = "" // 읽음 표시
)