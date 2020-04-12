package com.example.swimming.ui.map

import com.example.swimming.data.board.Board

data class MapFormStatus(
    var board: Board? = null,
    var error: String? = null,
    var like: Board? = null
)