package com.example.swimming.ui.board

// 게시글 관련 폼
data class BoardFormState(
    val titleError: Int? = null, // 제목 공백 체크
    val contentsError: Int? = null, // 내용 공백 체크

    val setId: String? = null,
    val setTitle: String? = null,
    val setContents: String? = null,
    val setTime: String? = null,
    val setImgCount: String? = null,

    val error: Int? = null,
    val loading: Int? = null
)