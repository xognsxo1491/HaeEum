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

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    var name: EditText? = null
    var id: EditText? = null
    var password: EditText? = null
    var passwordCheck: EditText? = null
    var email: EditText? = null
    var code: EditText? = null

    private var stateName: Boolean = false
    private var stateId: Boolean = false
    private var statePassword: Boolean = false
    private var statePasswordCheck: Boolean = false
    private var stateEmail: Boolean = false
    private var stateCode: Boolean = false

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

        if (stateName && stateId && statePassword && statePasswordCheck && stateEmail && stateCode) {
            val register = repository.register(name!!.text.toString(), id!!.text.toString(), password!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ userActionResult?.onSuccessRegister() },
                    { t: Throwable? -> when {
                            t!!.message.equals("IdDuplicate") -> userActionResult?.onDuplicateId()
                            else -> userActionResult?.onError()
                        }
                    })

            _registerForm.value = RegisterFormState(isProgressValid = true)
            disposables.add(register)

        } else userActionResult?.onFailed()
    }

    // 아이디 찾기
    fun findId() {
        checkName()
        checkEmail()

        if (stateName && stateEmail) {
            val findId = repository.findId(name!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("onErrorFind") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormState(isProgressValid = true)
            disposables.add(findId)
        }
    }

    // 비밀번호 찾기
    fun findPassword() {
        checkName()
        checkId()
        checkEmail()

        if (stateName && stateId  && stateEmail) {
            val findPassword = repository.findPassword(name!!.text.toString(), id!!.text.toString(), email!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("onErrorFind") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormState(isProgressValid = true)
            disposables.add(findPassword)
        }
    }

    // 이메일 보내기
    fun sendEmail() {
        if (email!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_isBlank)
            stateEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_register_email_format)
            stateEmail = false
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

        _registerForm.value = RegisterFormState(isProgressValid = true)
        disposables.add(sendEmail)
    }

    // 로그인
    fun login() {
        checkId()
        checkPassword()

        if (stateId && statePassword) {
            val login = repository.login(id!!.text.toString(), password!!.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result?.onSuccess()},
                    { t: Throwable? -> when {
                            t!!.message.equals("LoginFailed") -> result?.onFailed()
                            else -> result?.onError()
                        }
                    })

            _registerForm.value = RegisterFormState(isProgressValid = true)
            disposables.add(login)
        }
    }

    // 이름 형식 체크
    private fun checkName() {
        val pattern = Pattern.compile("^[a-zA-Z0-9]+$")

        if (name!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(nameError = R.string.message_isBlank)
            stateName = false
            return

        } else if (name!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(nameError = R.string.message_register_blank)
            stateName = false
            return

        } else if (name!!.text.toString().length < 2 || name!!.text.toString().length > 10) {
            _registerForm.value =
                RegisterFormState(nameError = R.string.message_register_name_length)
            stateName = false
            return

        } else if (pattern.matcher(name!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormState(nameError = R.string.message_register_name_form)
            stateName = false
            return

        } else stateName = true
    }

    // 아이디 형식 체크
    private fun checkId() {
        val pattern = Pattern.compile("^[a-zA-Z0-9]+$")

        if (id!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(idError = R.string.message_isBlank)
            stateId = false
            return

        } else if (id!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(idError = R.string.message_register_blank)
            stateId = false
            return

        } else if (id!!.text.toString().length < 6 || id!!.text.toString().length > 20) {
            _registerForm.value = RegisterFormState(idError = R.string.message_register_id_length)
            stateId = false
            return

        } else if (!pattern.matcher(id!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormState(idError = R.string.message_register_id_form)
            stateId = false
            return

        } else stateId = true
    }

    // 비밀번호 형식 체크
    private fun checkPassword() {
        if (password!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_isBlank)
            statePassword = false
            return

        } else if (password!!.text.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_register_blank)
            statePassword = false
            return

        } else if (password!!.text.toString().length < 8 || password!!.text.toString().length > 30) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_register_password_length)
            statePassword = false
            return

        } else statePassword = true
    }

    // 비민번호 확인 형식 체크
    private fun checkPasswordCheck() {
        if (passwordCheck!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(passwordCheckError = R.string.message_isBlank)
            statePasswordCheck = false
            return

        } else if (passwordCheck!!.text.toString() != password!!.text.toString()) {
            _registerForm.value = RegisterFormState(passwordCheckError = R.string.message_register_password_incorrect)
            statePasswordCheck = false
            return

        } else statePasswordCheck = true
    }

    // 이메일 형식 체크
    private fun checkEmail() {

        if (email!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_isBlank)
            stateEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email!!.text.toString()).matches()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_register_email_format)
            stateEmail = false
            return

        } else stateEmail = true
    }

    // 코드 형식 체크
    private fun checkCode() {
        if (code!!.text.toString().isEmpty()) {
            _registerForm.value = RegisterFormState(codeError = R.string.message_isBlank)
            stateCode = false
            return

        } else if (code!!.text.toString() != mRandom.toString()) {
            _registerForm.value = RegisterFormState(codeError = R.string.message_register_code_incorrect)
            stateCode = false
            return

        } else stateCode = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}