package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class BoardDataSource {

    // 게시글 작성하기
    fun writeBoard(title: String, contents: String, context: Context, uuid: String, imgCount: String, commentCount: String, path1: String, path2: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")

                val board = Board(UtilBase64Cipher.encode(userId.toString()), title, contents, UtilBase64Cipher.encode(System.currentTimeMillis().toString()), uuid, imgCount, commentCount)
                database.child(uuid).setValue(board)
                it.onComplete()
            }
        })
    }

    // 게시글 불러오기
    fun loadBoardList(owner: LifecycleOwner, path1: String, path2: String) : DatabasePagingOptions<Board> {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2)

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
    fun infoBoard(path1: String, path2: String, child: String) = Single.create<Board> {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2).child(child)
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
                    ref.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                }

                "4" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    ref.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                    ref.child(path).child("$uuid/4").putFile(data.clipData!!.getItemAt(3).uri)
                }

                "5" -> {
                    ref.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    ref.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    ref.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                    ref.child(path).child("$uuid/4").putFile(data.clipData!!.getItemAt(3).uri)
                    ref.child(path).child("$uuid/5").putFile(data.clipData!!.getItemAt(4).uri)
                }
            }
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    // 이미지 불러오기
    fun loadImage(path: String, count: String) = Observable.create<String> { emitter ->
        when (count) {
            "1" -> {

                FirebaseStorage.getInstance().getReference("$path/1").downloadUrl.addOnSuccessListener {
                   emitter.onNext("이미지0: $it" )
                }
            }

            "2" -> {
                FirebaseStorage.getInstance().getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }
            }

            "3" -> {
                FirebaseStorage.getInstance().getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }
            }

            "4" -> {
                FirebaseStorage.getInstance().getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/4").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지4: $it")
                }
            }

            "5" -> {
                FirebaseStorage.getInstance().getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/4").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지4: $it")
                }

                FirebaseStorage.getInstance().getReference("$path/5").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지5: $it")
                }
            }
        }
    }

    // 댓글 작성
    fun uploadComments(path1: String, path2: String, child: String, num: String, id: String, time: String, contents: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2).child(child)
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

    // 댓글 불러오기
    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, child: String) : DatabasePagingOptions<Comments> {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2).child(child)

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

    // 댓글 개수 불러오기
    fun loadCommentCount(path1: String, path2: String, uuid: String) = Single.create<String> {
        val query = FirebaseDatabase.getInstance().reference.child(path1).child(path2).child(uuid)
        query.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                it.onSuccess(p0.value.toString())
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                it.onSuccess(p0.value.toString())
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    fun updateCommentCount(path1: String, path2: String, uuid: String, num: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path1).child(path2).child(uuid)
        val map: HashMap<String, Any> = HashMap()
        map["commentCount"] = num
        database.updateChildren(map)
    }
}