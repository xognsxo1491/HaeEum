package com.example.swimming.ui.map

import com.example.swimming.data.board.Board

// 지도 관련(유동성)
data class MapFormStatus(
    var board: Board? = null,
    var error: String? = null,
    var like: Board? = null
)