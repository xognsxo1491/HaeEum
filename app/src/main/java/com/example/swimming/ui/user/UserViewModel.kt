package com.example.swimming.ui.user

import android.content.Context
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

class UserViewModel(private val repository: UserRepository, private val context: Context) : ViewModel() {
    private val mRandom = System.currentTimeMillis()

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    var name: String? = null
    var id: String? = null
    var password: String? = null
    var passwordCheck: String? = null
    var email: String? = ""
    var code: String? = null

    private var stateName: Boolean = false
    private var stateId: Boolean = false
    private var statePassword: Boolean = false
    private var statePasswordCheck: Boolean = false
    private var stateEmail: Boolean = false
    private var stateCode: Boolean = false

    private val disposables = CompositeDisposable()
    var userActionResult: UserActionResult? = null
    var result: Result? = null

    fun register() {
        checkName()
        checkId()
        checkPassword()
        checkPasswordCheck()
        checkEmail()
        checkCode()

        if (stateName && stateId && statePassword && statePasswordCheck && stateEmail && stateCode) {
            val register = repository.register(name!!, id!!, password!!, email!!)
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

    fun findId() {
        checkName()
        checkEmail()

        if (stateName && stateEmail) {
            val findId = repository.findId(name!!, email!!)
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

    fun findPassword() {
        checkName()
        checkId()
        checkEmail()

        if (stateName && stateId  && stateEmail) {
            val findPassword = repository.findPassword(name!!, id!!, email!!)
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

    fun sendEmail() {
        if (email.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_isBlank)
            stateEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_register_email_format)
            stateEmail = false
            return
        }

        val sendEmail = repository.sendEmail(email!!, mRandom.toString())
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

    fun login() {
        checkId()
        checkPassword()

        if (stateId && statePassword) {
            val login = repository.login(id!!, password!!, context)
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

    private fun checkName() {
        if (name.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(nameError = R.string.message_isBlank)
            stateName = false
            return

        } else if (name.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(nameError = R.string.message_register_blank)
            stateName = false
            return

        } else if (name.toString().length < 2 || name.toString().length > 10) {
            _registerForm.value =
                RegisterFormState(nameError = R.string.message_register_name_length)
            stateName = false
            return

        } else stateName = true
    }

    private fun checkId() {
        if (id.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(idError = R.string.message_isBlank)
            stateId = false
            return

        } else if (id.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(idError = R.string.message_register_blank)
            stateId = false
            return

        } else if (id.toString().length < 6 || id.toString().length > 20) {
            _registerForm.value = RegisterFormState(idError = R.string.message_register_id_length)
            stateId = false
            return

        } else stateId = true
    }

    private fun checkPassword() {
        if (password.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_isBlank)
            statePassword = false
            return

        } else if (password.toString().contains(" ")) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_register_blank)
            statePassword = false
            return

        } else if (password.toString().length < 8 || password.toString().length > 30) {
            _registerForm.value = RegisterFormState(passwordError = R.string.message_register_password_length)
            statePassword = false
            return

        } else statePassword = true
    }

    private fun checkPasswordCheck() {
        if (passwordCheck.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(passwordCheckError = R.string.message_isBlank)
            statePasswordCheck = false
            return

        } else if (passwordCheck != password) {
            _registerForm.value = RegisterFormState(passwordCheckError = R.string.message_register_password_incorrect)
            statePasswordCheck = false
            return

        } else statePasswordCheck = true
    }

    private fun checkEmail() {
        if (email.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_isBlank)
            stateEmail = false
            return

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            _registerForm.value = RegisterFormState(emailError = R.string.message_register_email_format)
            stateEmail = false
            return

        } else stateEmail = true
    }

    private fun checkCode() {
        if (code.isNullOrEmpty()) {
            _registerForm.value = RegisterFormState(codeError = R.string.message_isBlank)
            stateCode = false
            return

        } else if (code != mRandom.toString()) {
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