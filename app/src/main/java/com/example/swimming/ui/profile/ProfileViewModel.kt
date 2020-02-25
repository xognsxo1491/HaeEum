package com.example.swimming.ui.profile

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swimming.R
import com.example.swimming.data.profile.ProfileRepository
import com.example.swimming.ui.result.ProfileActionResult
import com.example.swimming.utils.UtilBase64Cipher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class ProfileViewModel(private val repository: ProfileRepository, val context: Context) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _userForm = MutableLiveData<ProfileFormState>()
    val profileFormState: LiveData<ProfileFormState> = _userForm

    var profileImage: ImageView? = null
    var profileActionResult: ProfileActionResult? = null
    var data: Intent? = null

    // 프로필 불러오기
    fun setProfile() {
        val setProfile = repository.setProfile(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t: String? ->
                _userForm.value = ProfileFormState(
                    id = t!!.split(" ")[0]
                )
                _userForm.value = ProfileFormState(
                    email = t.split(" ")[1]
                )
            }

        disposables.add(setProfile)
    }

    // 프로필 이미지 업로드
    fun uploadProfileImage() {
        val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val id = pref.getString("Id","")

        val uploadProfile = repository.uploadProfileImage(id!!, data!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t ->
                _userForm.value =
                    ProfileFormState(uploadProfileImage = t)
                _userForm.value =
                    ProfileFormState(onLoading = R.string.loading)
                profileActionResult?.onLoad() },
                { _userForm.value =
                    ProfileFormState(onError = R.string.message_error)
                })

        disposables.add(uploadProfile)
    }

    fun downloadProfileImage() {
        val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val id = pref.getString("Id","")

        val downloadProfile = repository.downloadProfileImage(id!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ t ->
                _userForm.value = ProfileFormState(downloadProfileImage = t) }, {})

        disposables.add(downloadProfile)
    }

    // 로그아웃
    fun logout() {
        val logout = repository.logout(context)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {profileActionResult?.onLogout()}

        disposables.add(logout)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}