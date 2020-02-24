package com.example.swimming.data.board

data class Board(
    var id: String = "", // 아이디
    var title: String = "", // 제목
    var contents: String = "", // 내용
    var time: String = "", // 시간
    var uuid: String = "", // 게시글 고유 UUID
    var imgCount: String = "" // 업로드 이미지 개수
)