package com.example.swimming.data.map

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MapDataSource {

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
}