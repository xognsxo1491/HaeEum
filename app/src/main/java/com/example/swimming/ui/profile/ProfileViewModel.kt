package com.example.swimming.ui.profile

import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swimming.R
import com.example.swimming.data.profile.ProfileRepository
import com.example.swimming.ui.result.ProfileActionResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _profileForm = MutableLiveData<ProfileFormState>()
    val profileFormState: LiveData<ProfileFormState> = _profileForm

    var profileActionResult: ProfileActionResult? = null
    var progressBar: ProgressBar? = null
    var data: Intent? = null

    // 프로필 불러오기
    fun setProfile() {
        val setProfile = repository.setProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ t: String? ->
                _profileForm.value = ProfileFormState(
                    id = t!!.split(" ")[0]
                )
                _profileForm.value = ProfileFormState(
                    email = t.split(" ")[1]
                )
                progressBar!!.visibility = View.INVISIBLE
            }, {
                _profileForm.value = ProfileFormState(onError = R.string.message_error)
            })

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