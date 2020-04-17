package com.kang.swimming.data.board

// 댓글 정보
data class Comments(
    var uuid: String = "", // UUID
    var id: String= "", // 아이디
    var time: String = "", // 시간
    var contents: String ="", // 내용
    var kind: String = "" // 대댓글
)