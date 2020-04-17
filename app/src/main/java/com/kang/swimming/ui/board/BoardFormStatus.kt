package com.kang.swimming.ui.board

import com.kang.swimming.data.board.Board

// 게시글 관련 폼 (유동성)
data class BoardFormStatus(
    val titleError: Int? = null,
    val contentsError: Int? = null,
    val board: Board? = null,

    val setId: String? = null,
    val setTitle: String? = null,
    val setContents: String? = null,
    val setTime: String? = null,
    val setImgCount: String? = null,
    val setCommentCount: String? = null,
    val setLikeCount: String? = null,

    val image0: String? = null,
    val image1: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    val image4: String? = null,
    val image5: String? = null,

    val messageLike: Int? = null,
    val codeIntent: String? = null,

    val error: Int? = null,
    val loading: Int? = null,
    val check: Int? = null,
    val delete: String? = null
)