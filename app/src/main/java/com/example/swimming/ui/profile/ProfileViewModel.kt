package com.example.swimming.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swimming.R
import com.example.swimming.data.profile.Message
import com.example.swimming.data.profile.ProfileRepository
import com.example.swimming.adapter.MessageViewHolder
import com.example.swimming.ui.board.MyBoardActivity
import com.example.swimming.ui.result.ProfileActionResult
import com.example.swimming.ui.result.Result
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_message.view.*

// 프로필 뷰모델
class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _profileForm = MutableLiveData<ProfileFormStatus>()
    val profileFormStatus: LiveData<ProfileFormStatus> = _profileForm

    var recyclerView: RecyclerView? = null
    var refreshLayout: SwipeRefreshLayout? = null
    var switch: SwitchCompat? = null
    var switch2: SwitchCompat? = null

    var profileActionResult: ProfileActionResult? = null
    var progressBar: ProgressBar? = null
    var data: Intent? = null
    var result: Result? = null

    // 토큰
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
                _profileForm.value = ProfileFormStatus(
                    id = t!!.split(" ")[0],
                    email = t.split(" ")[1])

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

                if (UtilBase64Cipher.decode(p2.status) == "true") {
                    p0.itemView.text_notification_kind.setTextColor(Color.parseColor("#7E8188"))
                }

                p0.itemView.layout_message.setOnClickListener {
                    updateMessageStatus(path1, path2, p2.key)

                    val intent = Intent(context, MyBoardActivity::class.java)
                    intent.putExtra("Kind", "Board")
                    intent.putExtra("message", p2.uuid)
                    context!!.startActivity(intent)
                }

                p0.itemView.layout_message_delete.setOnClickListener {
                    deleteMessage(path1, path2, p2.key)
                    refresh()
                }
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

    // 알림 메시지 상태 수정
    fun updateMessageStatus(path1: String, path2: String, uuid: String) {
        val update = repository.updateMessageStatus(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(update)
    }

    // 알림 메세지 삭제
    fun deleteMessage(path1: String, path2: String, uuid: String) {
        val delete = repository.deleteMessage(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(delete)
    }

    // 이달의 물고기1
    fun showDictionary1(uuid: String) {
        val show = repository.showDictionary(uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profileForm.value = ProfileFormStatus(
                    board1 = it)
            }, {})

        disposables.add(show)
    }

    // 이달의 물고기2
    fun showDictionary2(uuid: String) {
        val show = repository.showDictionary(uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profileForm.value = ProfileFormStatus(
                    board2 = it)
            }, {})

        disposables.add(show)
    }

    // 이달의 물고기3
    fun showDictionary3(uuid: String) {
        val show = repository.showDictionary(uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _profileForm.value = ProfileFormStatus(
                    board3 = it)
            }, {})

        disposables.add(show)
    }

    // 알림 푸쉬 체크
    fun checkAlarm() {
        repository.checkAlarm(switch!!)
    }

    // 알림 탭 체크
    fun checkTab() {
        repository.checkTab(switch2!!)
    }

    // 알림 설정 불러오기
    fun loadStatusAlarm() {
        val status = repository.loadStatusAlarm()
        switch!!.isChecked = status
    }

    // 알림 설정 불러오기
    fun loadStatusTab() {
        val status = repository.loadStatusTab()
        switch2!!.isChecked = status
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}