package com.example.swimming.data.user

import android.content.Context
import com.example.swimming.utils.UtilSendEmail
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable

class UserDataSource {
    private lateinit var mAuth: FirebaseAuth

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // 회원가입
    fun register(name: String, id: String, password: String, email: String) = Completable.create {
            database.getReference("UserInfo").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        it.onError(p0.toException())
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.child(id).exists()) {
                            val user = User(
                                name,
                                id,
                                password,
                                email
                            )
                            database.reference.child("UserInfo").child(id).setValue(user)
                            database.reference.child("EmailInfo").child(email).setValue(id)
                            it.onComplete()

                            mAuth = FirebaseAuth.getInstance()
                            mAuth.createUserWithEmailAndPassword(UtilBase64Cipher.decode(email), password)

                        } else
                            it.onError(Throwable("IdDuplicate"))
                    }
                })
        }

    // 로그인
    fun login(id: String, password: String, context: Context) = Completable.create {
        database.getReference("UserInfo").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (p0.child("password").value.toString() == password) {
                        auth.signInWithEmailAndPassword(UtilBase64Cipher.decode(p0.child("email").value.toString()), UtilBase64Cipher.decode(password))

                        val pref = context.getSharedPreferences("Login",Context.MODE_PRIVATE)
                        val editor = pref.edit()

                        editor.putString("Id", UtilBase64Cipher.decode(id)).apply()
                        it.onComplete()

                    } else
                        it.onError(Throwable("LoginFailed"))
                } else
                    it.onError(Throwable("LoginFailed"))
            }
        })
    }

    // 이메일 전송
    fun sendEmail(email: String, code: String) = Completable.create {
        database.getReference("EmailInfo").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (!p0.child(email).exists()) {
                        try {
                            val sendEmail = UtilSendEmail()
                            sendEmail.sendMail("헤엄 인증코드 발송 메일입니다.", "인증번호는 다음과 같습니다.\n 인증번호: $code", UtilBase64Cipher.decode(email))
                            it.onComplete()

                        } catch (e: Exception) {
                            it.onError(e)
                        }

                    } else
                        it.onError(Throwable("EmailDuplicate"))
                }
            })
    }

    // 아이디 찾기
    fun findId(name: String, email: String) = Completable.create {
        val query = database.getReference("UserInfo").orderByChild(UtilBase64Cipher.encode("email"))
        query.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val user = p0.getValue(User::class.java)
                if (email == user?.email) {
                    p0.getValue(user::class.java)

                    if (name == user.name) {
                        try {
                            val sendEmail = UtilSendEmail()
                            sendEmail.sendMail("헤엄 아이디 발송 메일입니다.", "아이디는 다음과 같습니다.\n 아이디: " +
                                    UtilBase64Cipher.decode(p0.key.toString()), UtilBase64Cipher.decode(email))
                            it.onComplete()

                        } catch (e: Exception) {
                            it.onError(e)
                            e.printStackTrace()
                        }
                    } else
                        it.onError(Throwable("onErrorFind"))
                } else
                    it.onError(Throwable("onErrorFind"))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun findPassword(name: String, id: String, email: String) = Completable.create {
        val query = database.getReference("UserInfo").orderByChild(UtilBase64Cipher.encode("email"))
        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val user = p0.getValue(User::class.java)
                if (email == user?.email) {
                    p0.getValue(user::class.java)

                    if (name == user.name && id == user.id) {

                        try {
                            val sendEmail = UtilSendEmail()
                            sendEmail.sendMail("헤엄 비밀번호 찾기 발송 메일입니다.", "비밀번호는 다음과 같습니다.\n 비밀번호: " +
                                        UtilBase64Cipher.decode(user.password.toString()), UtilBase64Cipher.decode(email))
                            it.onComplete()

                        } catch (e: Exception) {
                            it.onError(e)
                            e.printStackTrace()
                        }

                    } else
                        it.onError(Throwable("onErrorFind"))
                } else
                    it.onError(Throwable("onErrorFind"))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}