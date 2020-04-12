package com.example.swimming.data.profile

import android.content.SharedPreferences
import android.net.Uri
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.data.board.Board
import com.example.swimming.data.user.User
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ProfileDataSource {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val storage: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val instanceId: FirebaseInstanceId by lazy {
        FirebaseInstanceId.getInstance()
    }

    // 토큰 확인
    fun checkToken(editor: SharedPreferences.Editor) = Completable.create {
        instanceId.instanceId.addOnCompleteListener {
            val token = it.result?.token
            editor.putString("token", token).apply()
        }
    }

    // 프로필 설정
    fun setProfile(id: String) = Observable.create<String> {
        database.reference.child("User").child("UserInfo").child(UtilBase64Cipher.encode(id))
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    val email = user!!.email

                    it.onNext("$id $email")
                    it.onComplete()
                }
            })
    }

    // 로그아웃
    fun logout(editor: SharedPreferences.Editor) = Completable.create {
        editor.clear().apply()
        auth.signOut()
        it.onComplete()
    }

    // 알림 체크
    fun checkMessage(owner: LifecycleOwner, path1: String, path2: String, id: String) : DatabasePagingOptions<Message> {
        val reference = database.reference.child(path1).child(path2).child(id)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Message>()
            .setLifecycleOwner(owner)
            .setQuery(reference, config, Message::class.java)
            .build()
    }

    // 알림 읽음 표시
    fun updateMessageStatus(path1: String, path2: String, id: String, uuid: String) = Completable.create {
        val reference = database.reference.child(path1).child(path2).child(id).child(uuid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val map: HashMap<String, Any> = HashMap()
                map["status"] = UtilBase64Cipher.encode("true")

                reference.updateChildren(map)
            }
        })
    }

    // 메세지 지우기
    fun deleteMessage(path1: String, path2: String, id: String, uuid: String) = Completable.create {
        database.reference.child(path1).child(path2).child(id).child(uuid).removeValue()
    }

    // 이달의 물고기
    fun showDictionary(uuid: String) = Single.create<Board> {
        database.reference.child("Dictionary").child("DictionaryInfo").child(uuid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val board = p0.getValue(Board::class.java)
                it.onSuccess(board!!)
            }
        })
    }

    // 이달의 물고기 이미지
    fun showDictionaryImage(path: String) = Single.create<Uri> { emitter ->
        storage.reference.child("Dictionary").child("$path/1").downloadUrl.addOnSuccessListener {
            emitter.onSuccess(it)
        }
    }
}