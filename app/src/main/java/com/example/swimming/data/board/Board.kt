package com.example.swimming.data.board

// 게시글 정보
data class Board(
    var kind: String = "",
    var id: String = "",
    var token: String = "",
    var title: String = "",
    var contents: String = "",
    var time: String = "",
    var uuid: String = "",
    var imgCount: String = "",
    var commentCount: String = "",
    var like: String = "",
    var store: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)