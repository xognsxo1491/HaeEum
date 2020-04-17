package com.kang.swimming.data.board

// 게시글 정보
data class Board(
    var kind: String = "", // 게시글 종류
    var id: String = "", // 아이디
    var token: String = "", // FCM 토큰
    var title: String = "", // 제목
    var contents: String = "", // 내용
    var time: String = "", // 시간
    var uuid: String = "", // UUID
    var imgCount: String = "", // 이미지 개수
    var commentCount: String = "", // 댓글 개수
    var like: String = "", // 좋아요 개수
    var store: String = "", // 수족관 이름
    var latitude: Double = 0.0, // 좌표
    var longitude: Double = 0.0 // 좌표
)