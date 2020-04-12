package com.example.swimming.data.board

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.data.profile.Message
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.URL

class BoardDataSource {

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    // 게시글 작성
    fun writeBoard(token: String, title: String, contents: String, context: Context, time: String, uuid: String, imgCount: String, commentCount: String, like: String, path1: String, path2: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")

                val board = Board(UtilBase64Cipher.encode(path1), UtilBase64Cipher.encode(userId.toString()), token, title, contents, time, uuid, imgCount, commentCount, like)
                reference.child(uuid).setValue(board)
                it.onComplete()
            }
        })
    }

    // 게시글 작성 (수조관 게시판)
    // 게시글 작성
    fun writeBoard2(token: String, title: String, contents: String, context: Context, time: String, uuid: String, imgCount: String, commentCount: String, like: String, path1: String, path2: String, store: String, latitude: Double, longitude: Double) = Completable.create {
        val reference = database.reference.child(path1).child(path2)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")

                val board = Board(UtilBase64Cipher.encode(path1), UtilBase64Cipher.encode(userId.toString()), token, title, contents, time, uuid, imgCount, commentCount, like, store, latitude, longitude)
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

            else -> {
                for (i in 1..Integer.parseInt(count)) {
                    storage.reference.child(path1).child(uuid).child(i.toString()).delete()
                }
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
    fun uploadImage(path: String, uuid: String, count: String, data: Intent?, context: Context) = Completable.create {
        try {
            when (count) {
                "1" -> {
                    val resize = resize(context, data!!.data!!)
                    val outputStream = ByteArrayOutputStream()
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val byte = outputStream.toByteArray()

                    storage.reference.child(path).child("$uuid/1").putBytes(byte)
                }

                else -> {
                    for (i in 1..Integer.parseInt(count)) {
                        val resize = resize(context, data!!.clipData!!.getItemAt(i - 1).uri)
                        val outputStream = ByteArrayOutputStream()
                        resize.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        val byte = outputStream.toByteArray()

                        storage.reference.child(path).child("$uuid/$i").putBytes(byte)
                    }
                }
            }
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    // 이미지 리사이즈
    private fun resize(context: Context, uri: Uri): Bitmap {
        lateinit var resizedBitmap: Bitmap

        val options = BitmapFactory.Options()
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

            var width = options.outWidth
            var height = options.outHeight
            var sampleSize = 1

            while (true) {
                if (width / 2 < 500 || height / 2 < 500)
                    break
                width /= 2
                height /= 2
                sampleSize *= 2
           }

            options.inSampleSize = sampleSize
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)
            resizedBitmap = bitmap!!
        } catch (e: FileNotFoundException) {

        }
        return resizedBitmap
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
                val comments = Comments(uuid2, id, time, contents)
                reference.child(uuid2).setValue(comments)
                it.onComplete()
            }
        })
    }

    // 댓글 삭제
    fun deleteComments(path1: String, path2: String, path3: String, uuid: String) = Completable.create {
        val comment = database.reference.child(path1).child(path2).child(path3).child(uuid)
        comment.removeValue()
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

    // 댓글 개수 업데이트 (플러스)
    fun updateCommentCountPlus(path1: String, path2: String, uuid: String) = Completable.create {
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

    // 댓글 개수 업데이트 (마이너스)
    fun updateCommentCountMinus(path1: String, path2: String, uuid: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                val num = Integer.parseInt(UtilBase64Cipher.decode(board!!.commentCount))

                val map: HashMap<String, Any> = HashMap()
                map["commentCount"] = UtilBase64Cipher.encode((num - 1).toString())

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

    // 좋아요 개수 업데이트 마이너스
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

            }
        })
    }

    // 내가 쓴 글
    fun myBoard(path1: String, path2: String, id: String) = Observable.create<Board> {
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

                if (UtilBase64Cipher.decode(board!!.id) == id) {
                    it.onNext(board)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    // 댓글 단 글
    fun myComments(path1: String, path2: String, path3: String, id: String) = Observable.create<Board> {
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
                val split = p0.value.toString().split(", id=")[1]
                val splitId = split.split(", time=")[0]

                if (splitId == id)
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
            override fun onChildRemoved(p0: DataSnapshot) {

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
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

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
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    // 토큰 메시지 전달
    fun pushToken(title: String, contents: String, token: String, fcm: String, key: String) = Completable.create {
        try {
            val root = JSONObject()
            val notification = JSONObject()

            notification.put("title", title)
            notification.put("body", contents)
            root.put("notification", notification)
            root.put("to", token)

            val url = URL(fcm)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.doInput = true
            conn.addRequestProperty("Authorization", "key=$key")
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Content-Type", "application/json")

            val os = conn.outputStream
            os.write(root.toString().toByteArray())
            os.flush()

            conn.responseCode

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // 나의 알림
    fun pushMessage(path1: String, path2: String, id: String, uuid1: String, uuid2: String, kind: String, title: String, contents: String, time: String, status: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(id)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val message = Message(uuid2, uuid1, kind, title, contents, time, status)
                reference.child(uuid2).setValue(message)
            }
        })
    }
}