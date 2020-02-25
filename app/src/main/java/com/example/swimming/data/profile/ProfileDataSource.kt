package com.example.swimming.data.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.swimming.data.user.User
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
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

    // 프로필 설정
    fun setProfile(context: Context) = Observable.create<String> {
        val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val id = pref.getString("Id", "")

        database.getReference("UserInfo").child(UtilBase64Cipher.encode(id!!))
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

    // 프로필 업로드
    fun uploadProfileImage(id: String, data: Intent?) = Single.create<Uri> { emitter ->
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("Profiles/$id")

        ref.putFile(data!!.data!!).addOnSuccessListener {
            emitter.onSuccess(data.data!!)

        }.addOnFailureListener {
            emitter.onError(it)
        }
    }

    fun downloadProfileImage(id: String) = Single.create<Uri> { emitter ->
        val storage = FirebaseStorage.getInstance().getReference("Profiles/$id").downloadUrl
        storage.addOnSuccessListener {
            emitter.onSuccess(it)

        }.addOnFailureListener {
            emitter.onError(it)
        }
    }

    // 로그아웃
    fun logout(context: Context) = Completable.create {
        val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor = pref.edit()

        editor.clear().apply()
        auth.signOut()
        it.onComplete()
    }
}