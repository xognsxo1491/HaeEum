package com.example.swimming.data.profile

import android.content.SharedPreferences
import com.example.swimming.data.user.User
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ProfileDataSource {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val instanceId: FirebaseInstanceId by lazy {
        FirebaseInstanceId.getInstance()
    }


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
}