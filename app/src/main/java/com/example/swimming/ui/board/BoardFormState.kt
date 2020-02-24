package com.example.swimming.ui.board

import android.net.Uri

data class BoardFormState(
    val titleError: Int? = null,
    val contentsError: Int? = null,

    val setId: String? = null,
    val setTitle: String? = null,
    val setContents: String? = null,
    val setTime: String? = null,
    val setImage: Uri? = null,

    val error: Int? = null,
    val loading: Int? = null
)