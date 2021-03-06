package com.kang.swimming.data.map

import com.kang.swimming.data.board.Board
import com.google.firebase.database.*
import io.reactivex.Observable
import io.reactivex.Single

// 지도 관련 데이터 소스
class MapDataSource {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    // 수족관 위치 불러오기
    fun showPosition(path1: String, path2: String) = Observable.create<Board> {
        database.reference.child(path1).child(path2).addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                // TO do
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // TO do
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                // TO do
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val board = p0.getValue(Board::class.java)
                it.onNext(board!!)
            }
        })
    }

    // 수족관 정보 불러오기
    fun boardInfo(path1: String, path2: String, uuid: String) = Single.create<Board> {
        database.reference.child(path1).child(path2).child(uuid).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                it.onSuccess(board!!)
            }
        })
    }

    // 좋아요 누른 글
    fun myLike(path1: String, path2: String, path3: String, id: String) = Observable.create<Board> {
        database.reference.child(path1).child(path2).addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                // TO do
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // TO do
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                // TO do
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (p0.hasChild(id)) {
                    database.reference.child(path1).child(path3).child(p0.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            it.onError(p0.toException())
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val board = p0.getValue(Board::class.java)
                            it.onNext(board!!)
                        }
                    })
                }
            }
        })
    }
}