package com.example.swimming.ui.user

import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swimming.R
import com.example.swimming.ui.result.UserActionResult
import com.example.swimming.data.user.UserRepository
import com.example.swimming.ui.result.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val mRandom = System.currentTimeMillis()

    private val _registerForm = MutableLiveData<RegisterFormStatus>()
    val registerFormStatus: LiveData<RegisterFormStatus> = _registerForm

    var name: EditText? = null
    var id: EditText? = null
    var password: EditText? = null
    var passwordCheck: EditText? = null
    var email: EditText? = null
    var code: EditText? = null

    private var statusName: Boolean = false
    private var statusId: Boolean = false
    private var statusPassword: Boolean = false
    private var statusPasswordCheck: Boolean = false
    private var statusEmail: Boolean = false
    private var statusCode: Boolean = false

    private val disposables = CompositeDisposable()
    var userActionResult: UserActionResult? = null
    var result: Result? = null

    // 회원가입
    fun register() {
        checkName()
        checkId()
        checkPassword()
        checkPasswordCheck()
        checkEmail()
        checkCode()

        if (statusName && statusId && statusPassword && statusPasswordCheck && statusEmail && statusCode) {
            val register = repository.register(name!!.text.toString(), id!!.text.toString(), password!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userActionResult?.onSuccessRegister() },
                    { t: Throwable? -> when {
                            t!!.message.equals("IdDuplicate") -> userActionResult?.onDuplicateId()
                            else -> userActionResult?.onError()
                        }
                    })

            _registerForm.value = RegisterFormStatus(isProgressValid = true)
            disposables.add(register)

        } else userActionResult?.onFailed()
    }

    // 아이디 찾기
    fun findId() {
        checkName()
        checkEmail()

        if (statusName && statusEmail) {
            val findId = repository.findId(name!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("onErrorFind") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormStatus(isProgressValid = true)
            disposables.add(findId)
        }
    }

    // 비밀번호 찾기
    fun findPassword() {
        checkName()
        checkId()
        checkEmail()

        if (statusName && statusId  && statusEmail) {
            val findPassword = repository.findPassword(name!!.text.toString(), id!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("onErrorFind") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormStatus(isProgressValid = true)
            disposables.add(findPassword)
        }
    }

    // 이메일 보내기
    fun sendEmail() {
        if (email!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(emailError = R.string.message_isBlank)
            statusEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormStatus(emailError = R.string.message_register_email_format)
            statusEmail = false
            return
        }

        val sendEmail = repository.sendEmail(email!!.text.toString(), mRandom.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe({ userActionResult?.onSuccessSend()},
                { t: Throwable? -> when {
                        t!!.message.equals("EmailDuplicate") -> userActionResult?.onDuplicateEmail()
                        else -> userActionResult?.onError()
                    }
                })

        _registerForm.value = RegisterFormStatus(isProgressValid = true)
        disposables.add(sendEmail)
    }

    // 로그인
    fun login() {
        checkId()
        checkPassword()

        if (statusId && statusPassword) {
            val login = repository.login(id!!.text.toString(), password!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("LoginFailed") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormStatus(isProgressValid = true)
            disposables.add(login)
        }
    }

    // 이름 형식 체크
    private fun checkName() {
        val pattern = Pattern.compile("^[a-zA-Z0-9]+$")

        if (name!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(nameError = R.string.message_isBlank)
            statusName = false
            return

        } else if (name!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormStatus(nameError = R.string.message_register_blank)
            statusName = false
            return

        } else if (name!!.text.toString().length < 2 || name!!.text.toString().length > 10) {
            _registerForm.value =
                RegisterFormStatus(nameError = R.string.message_register_name_length)
            statusName = false
            return

        } else if (pattern.matcher(name!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormStatus(nameError = R.string.message_register_name_form)
            statusName = false
            return

        } else statusName = true
    }

    // 아이디 형식 체크
    private fun checkId() {
        val pattern = Pattern.compile("^[a-zA-Z0-9]+$")

        if (id!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(idError = R.string.message_isBlank)
            statusId = false
            return

        } else if (id!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormStatus(idError = R.string.message_register_blank)
            statusId = false
            return

        } else if (id!!.text.toString().length < 6 || id!!.text.toString().length > 20) {
            _registerForm.value = RegisterFormStatus(idError = R.string.message_register_id_length)
            statusId = false
            return

        } else if (!pattern.matcher(id!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormStatus(idError = R.string.message_register_id_form)
            statusId = false
            return

        } else statusId = true
    }

    // 비밀번호 형식 체크
    private fun checkPassword() {
        if (password!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(passwordError = R.string.message_isBlank)
            statusPassword = false
            return

        } else if (password!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormStatus(passwordError = R.string.message_register_blank)
            statusPassword = false
            return

        } else if (password!!.text.toString().length < 8 || password!!.text.toString().length > 30) {
            _registerForm.value = RegisterFormStatus(passwordError = R.string.message_register_password_length)
            statusPassword = false
            return

        } else statusPassword = true
    }

    // 비민번호 확인 형식 체크
    private fun checkPasswordCheck() {
        if (passwordCheck!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(passwordCheckError = R.string.message_isBlank)
            statusPasswordCheck = false
            return

        } else if (passwordCheck!!.text.toString() != password!!.text.toString()) {
            _registerForm.value = RegisterFormStatus(passwordCheckError = R.string.message_register_password_incorrect)
            statusPasswordCheck = false
            return

        } else statusPasswordCheck = true
    }

    // 이메일 형식 체크
    private fun checkEmail() {

        if (email!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(emailError = R.string.message_isBlank)
            statusEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormStatus(emailError = R.string.message_register_email_format)
            statusEmail = false
            return

        } else statusEmail = true
    }

    // 코드 형식 체크
    private fun checkCode() {
        if (code!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormStatus(codeError = R.string.message_isBlank)
            statusCode = false
            return

        } else if (code!!.text.toString() != mRandom.toString()) {
            _registerForm.value = RegisterFormStatus(codeError = R.string.message_register_code_incorrect)
            statusCode = false
            return

        } else statusCode = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}