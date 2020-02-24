package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.utils.UtilBase64Cipher
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

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
    fun load(owner: LifecycleOwner, path: String) : DatabasePagingOptions<Board> {
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
    fun loadInfo(path: String, child: String) = Single.create<Board> {
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

    // 프로필 이미지 불러오기
    fun loadProfileImage(id: String) = Observable.create<Uri> { emitter ->
        val storage = FirebaseStorage.getInstance().getReference("Profile/$id").downloadUrl
        storage.addOnSuccessListener {
            emitter.onNext(it)
            emitter.onComplete()

        }.addOnFailureListener {
            emitter.onError(it)
        }
    }

    // 이미지 업로드
    fun uploadImage(path1: String, uuid: String ,count: String, data: Intent?) = Completable.create {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference

        when (count) {
            "1" -> {
                ref.child(path1).child("$uuid/1").putFile(data!!.data!!)
            }

            "2" -> {
                ref.child(path1).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
            }

            "3" -> {
                ref.child(path1).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
            }

            "4" -> {
                ref.child(path1).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(3).uri)
            }

            "5" -> {
                ref.child(path1).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(2).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(3).uri)
                ref.child(path1).child("$uuid/2").putFile(data.clipData!!.getItemAt(4).uri)
            }
        }
    }
}