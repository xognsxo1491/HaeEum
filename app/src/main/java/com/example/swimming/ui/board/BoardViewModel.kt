package com.example.swimming.ui.board

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.data.board.BoardRepository
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.PostViewHolder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BoardViewModel(val repository: BoardRepository, val context: Context) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val _writeForm = MutableLiveData<WriteFormState>()
    val writeFormState: LiveData<WriteFormState> = _writeForm

    private var stateTitle: Boolean = false
    private var stateContents: Boolean = false

    var recyclerView: RecyclerView? = null
    var refreshLayout: SwipeRefreshLayout? = null
    var result: Result? = null
    var title: String? = null
    var contents: String? = null

    fun write(path: String) {
        checkTitle()
        checkContents()

        if (stateTitle && stateContents) {
            val write = repository.write(title!!, contents!!, context, path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result?.onSuccess() }, { result?.onError() })

            disposables.add(write)
        }
    }

    fun load(owner: LifecycleOwner, path: String) {
        val layoutManager = LinearLayoutManager(context)

        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = layoutManager

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        val option = repository.load(owner, path)

        val adapter = object : FirebaseRecyclerPagingAdapter<Board, PostViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                return PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))
            }

            override fun onBindViewHolder(p0: PostViewHolder, p1: Int, p2: Board) {
                p0.setItem(p2)
                p0.onClick(p0.itemView, context)
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
                        Toast.makeText(context, R.string.message_error, Toast.LENGTH_SHORT).show()
                    }

                    LoadingState.FINISHED -> {
                        refreshLayout!!.isRefreshing = false
                    }
                }
            }

            override fun onError(databaseError: DatabaseError) {
                super.onError(databaseError)
                Toast.makeText(context, R.string.message_error, Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView!!.adapter = adapter
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        refreshLayout!!.setOnRefreshListener { adapter.refresh() }
    }

    fun loadInfo(id: TextView, title: TextView, contents: TextView, time: TextView, profile: ImageView, path: String, child: String) {
        repository.loadInfo(id, title, contents, time, profile, path, child)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun checkTitle() {
        if (title.isNullOrEmpty()) {
            _writeForm.value = WriteFormState(titleError = R.string.message_isBlank)
            return

        } else
            stateTitle = true
    }

    private fun checkContents() {
        if (contents.isNullOrEmpty()) {
            _writeForm.value = WriteFormState(contentsError = R.string.message_isBlank)
            return

        } else
            stateContents = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}