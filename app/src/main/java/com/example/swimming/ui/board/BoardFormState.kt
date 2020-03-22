package com.example.swimming.ui.board

import com.example.swimming.data.board.Board

// 게시글 관련 폼
data class BoardFormState(
    val titleError: Int? = null, // 제목 공백 체크
    val contentsError: Int? = null, // 내용 공백 체크
    val board: Board? = null,

    val setId: String? = null,
    val setTitle: String? = null,
    val setContents: String? = null,
    val setTime: String? = null,
    val setImgCount: String? = null,
    val setCommentCount: String? = null,

    val image0: String? = null,
    val image1: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    val image4: String? = null,
    val image5: String? = null,

    val codeIntent: String? = null,

    val error: Int? = null,
    val loading: Int? = null,
    val check: Int? = null
)