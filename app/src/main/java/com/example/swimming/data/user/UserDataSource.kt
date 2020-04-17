package com.example.swimming.data.user

import android.content.SharedPreferences
import com.example.swimming.etc.utils.UtilSendEmail
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.Single

// 유저 정보 관련 데이터 소스
class UserDataSource {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    // 회원가입
    fun register(name: String, id: String, password: String, email: String) = Completable.create {
            database.reference.child("User").child("UserInfo").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        it.onError(p0.toException())
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.child(id).exists()) {
                            val user = User(name, id, password, email)
                            database.reference.child("User").child("UserInfo").child(id).setValue(user)
                            database.reference.child("User").child("EmailInfo").child(email).setValue(id)
                            it.onComplete()

                            auth.createUserWithEmailAndPassword(UtilBase64Cipher.decode(email), password)

                        } else
                            it.onError(Throwable("IdDuplicate"))
                    }
                })
        }

    // 로그인
    fun login(id: String, password: String, editor: SharedPreferences.Editor) = Completable.create {
        database.reference.child("User").child("UserInfo").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if (p0.child("password").value.toString() == password) {
                        editor.putString("Id", UtilBase64Cipher.decode(id)).apply()
                        editor.putString("email", p0.child("email").value.toString()).apply()
                        editor.putString("password", p0.child("password").value.toString()).apply()

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
        database.reference.child("User").child("EmailInfo").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    it.onError(p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (!p0.child(email).exists()) {
                        try {
                            UtilSendEmail().sendMail("헤엄 인증코드 발송 메일입니다.", "인증번호는 다음과 같습니다.\n 인증번호: $code", UtilBase64Cipher.decode(email))
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
        val query = database.reference.child("User").child("UserInfo").orderByChild(
            UtilBase64Cipher.encode("email"))
        query.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                // To do
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // To do
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                // To do
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val user = p0.getValue(User::class.java)
                if (email == user?.email) {
                    p0.getValue(user::class.java)

                    if (name == user.name) {
                        try {
                            UtilSendEmail()
                                .sendMail("헤엄 아이디 발송 메일입니다.", "아이디는 다음과 같습니다.\n 아이디: " +
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
        })
    }

    // 비밀번호 찾기
    fun findPassword(name: String, id: String, email: String) = Completable.create {
        val query = database.reference.child("User").child("UserInfo").orderByChild(
            UtilBase64Cipher.encode("email"))
        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                // To do
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                // To do
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                // To do
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val user = p0.getValue(User::class.java)
                if (email == user?.email) {
                    p0.getValue(user::class.java)

                    if (name == user.name && id == user.id) {

                        try {
                            UtilSendEmail()
                                .sendMail("헤엄 비밀번호 찾기 발송 메일입니다.", "비밀번호는 다음과 같습니다.\n 비밀번호: " +
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
        })
    }

    // 비밀번호 확인 (회원탈퇴)
    fun confirmPassword(id: String, password: String) = Single.create<String> {
        database.reference.child("User").child("UserInfo").child(id).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (password == user!!.password) {
                    it.onSuccess(user.email!!)
                } else
                    it.onSuccess("error")
            }
        })
    }

    // 비밀번호 변경
    fun changePassword(id: String, password1: String, password3: String, editor: SharedPreferences.Editor) =  Single.create<String> {
        val reference = database.reference.child("User").child("UserInfo").child(id)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)

                if (password3 == user!!.password) {
                    it.onSuccess("duplicate")
                }

                else if (password1 == user.password && password3 != user.password) {
                    val map: HashMap<String, Any> = HashMap()
                    map["password"] = password3

                    reference.updateChildren(map)
                    editor.putString("Id", "").apply()
                    it.onSuccess("success")

                } else {
                    it.onSuccess("error")
                }
            }
        })
    }

    // 이메일 변경
    fun changeEmail(id: String, email: String, code1: String, code2: String) = Single.create<String> {
        val reference = database.reference.child("User").child("UserInfo").child(id)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (code1 == code2) {
                    val map: HashMap<String, Any> = HashMap()
                    map["email"] = email

                    reference.updateChildren(map)

                    it.onSuccess("success")
                } else {
                    it.onSuccess("error")
                }
            }
        })

    }

    // 이메일 전송
    fun sendEmailForChange(id: String, email1: String, email2: String, code: String) = Single.create<String> {
        database.reference.child("User").child("UserInfo").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)

                if (UtilBase64Cipher.encode(email2) == user!!.email) {
                    it.onSuccess("duplication")

                } else if (email1 == user.email && UtilBase64Cipher.encode(email2) != user.email) {
                    try {
                        UtilSendEmail()
                            .sendMail("헤엄 인증코드 발송 메일입니다.", "인증번호는 다음과 같습니다.\n 인증번호: $code", email2)
                        it.onSuccess("success")

                    } catch (e: Exception) {
                        it.onError(e)
                    }
                } else  {
                    it.onSuccess("error")
                }
            }
        })
    }

    // 이메일 전송 (회원 탈퇴)
    fun sendEmailForWithdraw(code: String, email: String) = Completable.create {
        try {
            UtilSendEmail().sendMail("헤엄 인증코드 발송 메일입니다.", "인증번호는 다음과 같습니다.\n 인증번호: $code", email)
            it.onComplete()
        } catch (e: Exception) {
            it.onError(e)
        }
    }

    // 회원 탈퇴
    fun withdraw(id: String, email: String, code: String, random: String, editor: SharedPreferences.Editor) = Single.create<String> { emitter ->
        if (code == random) {
            database.reference.child("User").child("UserInfo").child(id).removeValue().addOnSuccessListener {
                database.reference.child("User").child("EmailInfo").child(email).removeValue()
                auth.currentUser!!.delete()
                editor.clear().apply()
                emitter.onSuccess("success")
            }
        } else {
            emitter.onSuccess("error")
        }
    }
}