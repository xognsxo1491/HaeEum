package com.example.swimming.ui.profile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swimming.R
import com.example.swimming.data.profile.Message
import com.example.swimming.data.profile.ProfileRepository
import com.example.swimming.ui.adapter.MessageViewHolder
import com.example.swimming.ui.result.ProfileActionResult
import com.example.swimming.ui.result.Result
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _profileForm = MutableLiveData<ProfileFormStatus>()
    val profileFormStatus: LiveData<ProfileFormStatus> = _profileForm

    var recyclerView: RecyclerView? = null
    var refreshLayout: SwipeRefreshLayout? = null

    var profileActionResult: ProfileActionResult? = null
    var progressBar: ProgressBar? = null
    var data: Intent? = null
    var result: Result? = null

    fun checkToken() {
        val check = repository.checkToken()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(check)
    }

    // 프로필 불러오기
    fun setProfile() {
        val setProfile = repository.setProfile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t: String? ->
                _profileForm.value = ProfileFormStatus(id = t!!.split(" ")[0])
                _profileForm.value = ProfileFormStatus(email = t.split(" ")[1])
                val a = "asdasd"
                a.replace("a", "")

                progressBar!!.visibility = View.INVISIBLE
            }, {
                _profileForm.value = ProfileFormStatus(onError = R.string.message_error)
            })

        disposables.add(setProfile)
    }

    // 로그아웃
    fun logout() {
        val logout = repository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { profileActionResult?.onLogout() }

        disposables.add(logout)
    }

    // 알림 메세지
    fun checkMessage(owner: LifecycleOwner, path1: String, path2: String) {
        val option = repository.checkMessage(owner, path1, path2)
        var context: Context? = null

        val adapter = object : FirebaseRecyclerPagingAdapter<Message, MessageViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                context = parent.context
                return MessageViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.list_message, parent, false)
                )
            }

            override fun onBindViewHolder(p0: MessageViewHolder, p1: Int, p2: Message) {
                p0.setItem(p2)
                p0.onClick(p0.itemView, context!!)
                p0.profileForm.observe(owner, Observer {
                    val state = it ?: return@Observer

                    if (state.onClick != null) {
                        updateMessageStatus(path1, path2, p2.key)
                    }

                    if (state.onDelete != null) {
                        deleteMessage(path1, path2, p2.key)
                        state.onDelete = null
                        refresh()
                    }
                })
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        refreshLayout!!.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        refreshLayout!!.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        refreshLayout!!.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        refreshLayout!!.isRefreshing = false
                        result?.onError()
                    }

                    LoadingState.FINISHED -> {
                        refreshLayout!!.isRefreshing = false
                    }
                }
            }

            override fun onError(databaseError: DatabaseError) {
                super.onError(databaseError)
                result?.onError()
            }
        }

        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = adapter
        refreshLayout!!.setOnRefreshListener { adapter.refresh() }
    }

    fun updateMessageStatus(path1: String, path2: String, uuid: String) {
        val update = repository.updateMessageStatus(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(update)
    }

    fun deleteMessage(path1: String, path2: String, uuid: String) {
        val delete = repository.deleteMessage(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(delete)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}