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

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    // 게시글 작성
    fun writeBoard(title: String, contents: String, context: Context, uuid: String, imgCount: String, commentCount: String, like: String, path1: String, path2: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")

                val board = Board(UtilBase64Cipher.encode(userId.toString()), title, contents, UtilBase64Cipher.encode(System.currentTimeMillis().toString()), uuid, imgCount, commentCount, like)
                reference.child(uuid).setValue(board)
                it.onComplete()
            }
        })
    }

    // 게시글 삭제
    fun deleteBoard(path1: String, path2: String, path3: String, uuid: String, count: String) = Observable.create<Board> {
        val board = database.reference.child(path1).child(path2).child(uuid)
        val comment = database.reference.child(path1).child(path3).child(uuid)

        board.removeValue()
        comment.removeValue()

        when (count) {
            "1" -> {
                storage.reference.child(path1).child(uuid).child("1").delete()
            }

            "2" -> {
                storage.reference.child(path1).child(uuid).child("1").delete()
                storage.reference.child(path1).child(uuid).child("2").delete()
            }

            "3" -> {
                storage.reference.child(path1).child(uuid).child("1").delete()
                storage.reference.child(path1).child(uuid).child("2").delete()
                storage.reference.child(path1).child(uuid).child("3").delete()
            }

            "4" -> {
                storage.reference.child(path1).child(uuid).child("1").delete()
                storage.reference.child(path1).child(uuid).child("2").delete()
                storage.reference.child(path1).child(uuid).child("3").delete()
                storage.reference.child(path1).child(uuid).child("4").delete()
            }

            "5" -> {
                storage.reference.child(path1).child(uuid).child("1").delete()
                storage.reference.child(path1).child(uuid).child("2").delete()
                storage.reference.child(path1).child(uuid).child("3").delete()
                storage.reference.child(path1).child(uuid).child("4").delete()
                storage.reference.child(path1).child(uuid).child("5").delete()
            }
        }
    }

    // 게시글 삭제 조회
    fun checkBoard(path1: String, path2: String, uuid: String) = Completable.create {
        database.reference.child(path1).child(path2).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.hasChild(uuid))
                    it.onComplete()
            }
        })
    }

    // 게시글 불러오기
    fun loadBoardList(owner: LifecycleOwner, path1: String, path2: String) : DatabasePagingOptions<Board> {
        val reference = database.reference.child(path1).child(path2)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(40) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Board>()
            .setLifecycleOwner(owner)
            .setQuery(reference, config, Board::class.java)
            .build()
    }

    // 이미지 업로드
    fun uploadImage(path: String, uuid: String, count: String, data: Intent?) = Completable.create {

        try {
            when (count) {
                "1" -> {
                    storage.reference.child(path).child("$uuid/1").putFile(data!!.data!!)
                }

                "2" -> {
                    storage.reference.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    storage.reference.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                }

                "3" -> {
                    storage.reference.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    storage.reference.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    storage.reference.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                }

                "4" -> {
                    storage.reference.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    storage.reference.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    storage.reference.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                    storage.reference.child(path).child("$uuid/4").putFile(data.clipData!!.getItemAt(3).uri)
                }

                "5" -> {
                    storage.reference.child(path).child("$uuid/1").putFile(data!!.clipData!!.getItemAt(0).uri)
                    storage.reference.child(path).child("$uuid/2").putFile(data.clipData!!.getItemAt(1).uri)
                    storage.reference.child(path).child("$uuid/3").putFile(data.clipData!!.getItemAt(2).uri)
                    storage.reference.child(path).child("$uuid/4").putFile(data.clipData!!.getItemAt(3).uri)
                    storage.reference.child(path).child("$uuid/5").putFile(data.clipData!!.getItemAt(4).uri)
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
                storage.getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지0: $it" )
                }
            }

            "2" -> {
                storage.getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                storage.getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }
            }

            "3" -> {
                storage.getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                storage.getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                storage.getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }
            }

            "4" -> {
                storage.getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                storage.getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                storage.getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }

                storage.getReference("$path/4").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지4: $it")
                }
            }

            "5" -> {
                storage.getReference("$path/1").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지1: $it")
                }

                storage.getReference("$path/2").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지2: $it")
                }

                storage.getReference("$path/3").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지3: $it")
                }

                storage.getReference("$path/4").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지4: $it")
                }

                storage.getReference("$path/5").downloadUrl.addOnSuccessListener {
                    emitter.onNext("이미지5: $it")
                }
            }
        }
    }

    // 댓글 작성
    fun uploadComments(path1: String, path2: String, uuid1: String, uuid2: String, id: String, time: String, contents: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid1)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val comments = Comments(id, time, contents)
                reference.child(uuid2).setValue(comments)
                it.onComplete()
            }
        })
    }

    // 댓글 불러오기
    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, uuid: String) : DatabasePagingOptions<Comments> {
        val reference = database.reference.child(path1).child(path2).child(uuid)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Comments>()
            .setLifecycleOwner(owner)
            .setQuery(reference, config, Comments::class.java)
            .build()
    }

    // 댓글 개수 불러오기
    fun loadCommentCount(path1: String, path2: String, uuid: String) = Single.create<String> {
       database.reference.child(path1).child(path2).child(uuid).addListenerForSingleValueEvent(object : ValueEventListener {

           override fun onCancelled(p0: DatabaseError) {
               it.onError(p0.toException())
           }

           override fun onDataChange(p0: DataSnapshot) {
               val board = p0.getValue(Board::class.java)

               if (board != null)
                   it.onSuccess(board.commentCount)
           }
       })
    }

    // 댓글 개수 업데이트
    fun updateCommentCount(path1: String, path2: String, uuid: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                val num = Integer.parseInt(UtilBase64Cipher.decode(board!!.commentCount))

                val map: HashMap<String, Any> = HashMap()
                map["commentCount"] = UtilBase64Cipher.encode((num + 1).toString())

                reference.updateChildren(map)
            }
        })
    }

    // 좋아요 누르기
    fun uploadBoardLike(path1: String, path2: String, uuid: String, id: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                reference.child(id).setValue(id)
                it.onComplete()
            }
        })
    }

    // 좋아요 개수 업데이트 플러스
    fun updateBoardLikeCountPlus(path1: String, path2: String, uuid: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                val num = Integer.parseInt(UtilBase64Cipher.decode(board!!.like))

                val map: HashMap<String, Any> = HashMap()
                map["like"] = UtilBase64Cipher.encode((num + 1).toString())

                reference.updateChildren(map)
            }
        })
    }

    fun updateBoardLikeCountMinus(path1: String, path2: String, uuid: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                val num = Integer.parseInt(UtilBase64Cipher.decode(board!!.like))

                val map: HashMap<String, Any> = HashMap()
                map["like"] = UtilBase64Cipher.encode((num - 1).toString())

                reference.updateChildren(map)
            }
        })
    }

    // 좋아요 취소
    fun deleteBoardLike(path1: String, path2: String, uuid: String, id: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid).child(id)
        reference.removeValue()

        it.onComplete()
    }

    // 좋아요 개수 불러오기
    fun loadBoardLike(path1: String, path2: String, uuid: String) = Single.create<String> {
        database.reference.child(path1).child(path2).child(uuid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)

                if (board != null) {
                    it.onSuccess(board.like)
                }
            }
        })
    }

    // 좋아요 눌렀는지 체크
    fun checkBoardLike(path1: String, path2: String, uuid: String, id: String) = Completable.create {
        database.reference.child(path1).child(path2).child(uuid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null) {
                    if (p0.hasChild(id)) {
                        it.onComplete()
                    }
                }
            }
        })
    }

    // 키워드 검색
    fun searchKeyword(path1: String, path2: String, keyword: String) = Observable.create<Board> {
        database.reference.child(path1).child(path2).addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val board = p0.getValue(Board::class.java)

                if (UtilBase64Cipher.decode(board!!.title).contains(keyword) || UtilBase64Cipher.decode(board.contents).contains(keyword)) {
                    it.onNext(board)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }
}