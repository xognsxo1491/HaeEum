package com.example.swimming.ui.profile

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
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

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _userForm = MutableLiveData<ProfileFormState>()
    val profileFormState: LiveData<ProfileFormState> = _userForm

    var profileActionResult: ProfileActionResult? = null
    var progressBar: ProgressBar? = null
    var data: Intent? = null

    // 프로필 불러오기
    fun setProfile() {
        val setProfile = repository.setProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t: String? ->
                _userForm.value = ProfileFormState(
                    id = t!!.split(" ")[0]
                )
                _userForm.value = ProfileFormState(
                    email = t.split(" ")[1]
                )
                progressBar!!.visibility = View.INVISIBLE
            }

        disposables.add(setProfile)
    }

    // 로그아웃
    fun logout() {
        val logout = repository.logout()
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