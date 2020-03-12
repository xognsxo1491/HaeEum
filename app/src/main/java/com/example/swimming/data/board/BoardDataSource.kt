package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import io.reactivex.Completable
import io.reactivex.Single

class BoardDataSource {

    // 게시글 작성하기
    fun write(title: String, contents: String, context: Context, path: String, num: String, count: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")

                val board = Board(UtilBase64Cipher.encode(userId.toString()), title, contents, UtilBase64Cipher.encode(System.currentTimeMillis().toString()), num, count)
                database.child(num).setValue(board)
                it.onComplete()
            }
        })
    }

    // 게시글 불러오기
    fun downloadList(owner: LifecycleOwner, path: String) : DatabasePagingOptions<Board> {
        val database = FirebaseDatabase.getInstance().reference.child(path)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(40) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Board>()
            .setLifecycleOwner(owner)
            .setQuery(database, config, Board::class.java)
            .build()
    }

    // 게시글 정보
    fun downloadInfo(path: String, child: String) = Single.create<Board> {
        val database = FirebaseDatabase.getInstance().reference.child(path).child(child)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                it.onSuccess(board!!)
            }
        })
    }

    // 이미지 업로드
    fun uploadImage(path: String, uuid: String, count: String, data: Intent?) = Completable.create {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference

        try {
            when (count) {
                "1" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.data!!)
                }

                "2" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                }

                "3" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
                }

                "4" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(3).uri)
                }

                "5" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(3).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(4).uri)
                }
            }
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    // 댓글 작성
    fun uploadComments(path: String, child: String, num: String, id: String, time: String, contents: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path).child(child)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val comments = Comments(id, time, contents)
                database.child(num).setValue(comments)
                it.onComplete()
            }
        })
    }

    fun downloadComments(owner: LifecycleOwner, path: String, child: String) : DatabasePagingOptions<Comments> {
        val database = FirebaseDatabase.getInstance().reference.child(path).child(child)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Comments>()
            .setLifecycleOwner(owner)
            .setQuery(database, config, Comments::class.java)
            .build()
    }
}