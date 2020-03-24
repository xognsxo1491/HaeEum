package com.example.swimming.data.board

// 게시글 정보
data class Board(
    var id: String = "",
    var title: String = "",
    var contents: String = "",
    var time: String = "",
    var uuid: String = "",
    var imgCount: String = "",
    var commentCount: String = "",
    var like: String = ""
)