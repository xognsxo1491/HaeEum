package com.example.swimming.ui.board

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.data.board.BoardRepository
import com.example.swimming.data.board.Comments
import com.example.swimming.ui.result.Result
import com.example.swimming.utils.BoardViewHolder
import com.example.swimming.utils.CommentViewHolder
import com.example.swimming.utils.UtilBase64Cipher
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class BoardViewModel(val repository: BoardRepository, val context: Context) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val _boardForm = MutableLiveData<BoardFormState>()

    val boardFormState: LiveData<BoardFormState> = _boardForm

    private var stateTitle: Boolean = false
    private var stateContents: Boolean = false

    var recyclerViewBoard: RecyclerView? = null
    var recyclerViewComments: RecyclerView? = null
    var refreshLayoutBoard: SwipeRefreshLayout? = null
    var refreshLayoutComments: SwipeRefreshLayout? = null
    var result: Result? = null
    var title: String? = null
    var contents: String? = null

    var linearLayout : LinearLayout? = null
    var data: Intent? = null
    private var adapterComments: FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>? = null

    var img1 : ImageView? = null
    var img2 : ImageView? = null
    var img3 : ImageView? = null
    var img4 : ImageView? = null
    var img5 : ImageView? = null

    var card1 : CardView? = null
    var card2 : CardView? = null
    var card3 : CardView? = null
    var card4 : CardView? = null
    var card5 : CardView? = null

    // 작성하기
    fun write(path: String) {

        checkTitle()
        checkContents()

        if (stateTitle && stateContents) {
            _boardForm.value = BoardFormState(loading = R.string.loading)

            if (card1!!.visibility != View.VISIBLE) {

                val write = repository.write(title!!, contents!!, context, path, String.format("0"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result?.onSuccess() }, { result?.onError() })

                disposables.add(write)
            }

            if (card1!!.visibility == View.VISIBLE) {
                if (data!!.clipData != null) {
                    // 업로드할 이미지 여러개
                    when (data!!.clipData!!.itemCount) {
                        2 -> {
                            val uploadImage = repository.uploadImage("FreeBoard/","2", data)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.newThread())
                                .subscribe()

                            val write = repository.write(title!!, contents!!, context, path, String.format("2"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result?.onSuccess() }, { result?.onError() })

                            disposables.add(write)
                            disposables.add(uploadImage)
                        }

                        3 -> {
                            val uploadImage = repository.uploadImage("FreeBoard/","3", data)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.newThread())
                                .subscribe()

                            val write = repository.write(title!!, contents!!, context, path, String.format("3"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result?.onSuccess() }, { result?.onError() })

                            disposables.add(write)
                            disposables.add(uploadImage)
                        }

                        4 -> {
                            val uploadImage = repository.uploadImage("FreeBoard/","4", data)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.newThread())
                                .subscribe()

                            val write = repository.write(title!!, contents!!, context, path, String.format("4"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result?.onSuccess() }, { result?.onError() })

                            disposables.add(write)
                            disposables.add(uploadImage)
                        }

                        5 -> {
                            val uploadImage = repository.uploadImage("FreeBoard/","5", data)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.newThread())
                                .subscribe()

                            val write = repository.write(title!!, contents!!, context, path, String.format("5"))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result?.onSuccess() }, { result?.onError() })

                            disposables.add(write)
                            disposables.add(uploadImage)
                        }
                    }

                } else {
                    // 업로드 할 이미지가 1개
                    val uploadImage = repository.uploadImage("FreeBoard/","1", data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe()

                    val write = repository.write(title!!, contents!!, context, path, String.format("1"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result?.onSuccess() }, { result?.onError() })

                    disposables.add(write)
                    disposables.add(uploadImage)
                }
            }
        }
    }

    // 게시글 작성 이미지 선택
    fun setImage() {
        if (data!!.clipData != null) {

            if (data!!.clipData!!.itemCount > 5) {
                result?.onFailed()

            } else {
                when (data!!.clipData!!.itemCount) {
                    2 -> {
                        card1!!.visibility = View.VISIBLE
                        img1!!.setImageURI(data!!.clipData!!.getItemAt(0).uri)

                        card2!!.visibility = View.VISIBLE
                        img2!!.setImageURI(data!!.clipData!!.getItemAt(1).uri)
                    }

                    3 -> {
                        card1!!.visibility = View.VISIBLE
                        img1!!.setImageURI(data!!.clipData!!.getItemAt(0).uri)

                        card2!!.visibility = View.VISIBLE
                        img2!!.setImageURI(data!!.clipData!!.getItemAt(1).uri)

                        card3!!.visibility = View.VISIBLE
                        img3!!.setImageURI(data!!.clipData!!.getItemAt(2).uri)
                    }

                    4 -> {
                        card1!!.visibility = View.VISIBLE
                        img1!!.setImageURI(data!!.clipData!!.getItemAt(0).uri)

                        card2!!.visibility = View.VISIBLE
                        img2!!.setImageURI(data!!.clipData!!.getItemAt(1).uri)

                        card3!!.visibility = View.VISIBLE
                        img3!!.setImageURI(data!!.clipData!!.getItemAt(2).uri)

                        card4!!.visibility = View.VISIBLE
                        img4!!.setImageURI(data!!.clipData!!.getItemAt(3).uri)
                    }

                    5 -> {
                        card1!!.visibility = View.VISIBLE
                        img1!!.setImageURI(data!!.clipData!!.getItemAt(0).uri)

                        card2!!.visibility = View.VISIBLE
                        img2!!.setImageURI(data!!.clipData!!.getItemAt(1).uri)

                        card3!!.visibility = View.VISIBLE
                        img3!!.setImageURI(data!!.clipData!!.getItemAt(2).uri)

                        card4!!.visibility = View.VISIBLE
                        img4!!.setImageURI(data!!.clipData!!.getItemAt(3).uri)

                        card5!!.visibility = View.VISIBLE
                        img5!!.setImageURI(data!!.clipData!!.getItemAt(4).uri)
                    }
                }
            }

        } else {
            card1!!.visibility = View.VISIBLE
            img1!!.setImageURI(data!!.data)
        }
    }

    // 게시글 불러오기
    fun downloadList(owner: LifecycleOwner, path: String) {
        val layoutManager = LinearLayoutManager(context)

        recyclerViewBoard!!.setHasFixedSize(true)
        recyclerViewBoard!!.layoutManager = layoutManager

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        val option = repository.downloadList(owner, path)

        val adapter = object : FirebaseRecyclerPagingAdapter<Board, BoardViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
                return BoardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))
            }

            override fun onBindViewHolder(p0: BoardViewHolder, p1: Int, p2: Board) {
                p0.setItem(p2)
                p0.onClick(p0.itemView, context)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        refreshLayoutBoard!!.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        refreshLayoutBoard!!.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        refreshLayoutBoard!!.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        refreshLayoutBoard!!.isRefreshing = false
                        result?.onError()
                    }

                    LoadingState.FINISHED -> {
                        refreshLayoutBoard!!.isRefreshing = false
                    }
                }
            }

            override fun onError(databaseError: DatabaseError) {
                super.onError(databaseError)
                result?.onError()
            }
        }

        recyclerViewBoard!!.adapter = adapter
        refreshLayoutBoard!!.setOnRefreshListener { adapter.refresh() }
    }

    // 게시글 내용 불러오기
    fun downloadInfo(path: String, child: String)  {
        val loadInfo = repository.downloadInfo(path, child)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { t -> setBoard(t) }
            .doOnError { t: Throwable? -> throw t!! }
            .subscribe()

        disposables.add(loadInfo)
    }

    // 댓글 작성
    fun uploadComments(path: String, child: String, contents: String) {
        val upload = repository.uploadComments(path, child, contents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {adapterComments!!.refresh()}

        disposables.add(upload)

    }

    fun downloadComments(owner: LifecycleOwner, path: String, child: String) {
        val layoutManager = LinearLayoutManager(context)

        recyclerViewComments!!.setHasFixedSize(true)
        recyclerViewComments!!.layoutManager = layoutManager

        val option = repository.downloadComments(owner, path, child)

        adapterComments = object : FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                return CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false))
            }

            override fun onBindViewHolder(p0: CommentViewHolder, p1: Int, p2: Comments) {
                p0.setItem(p2)
                p0.onClick(p0.itemView, context)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        refreshLayoutComments!!.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        refreshLayoutComments!!.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        refreshLayoutComments!!.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        refreshLayoutComments!!.isRefreshing = false
                        result?.onError()
                    }

                    LoadingState.FINISHED -> {
                        refreshLayoutComments!!.isRefreshing = false
                    }
                }
            }

            override fun onError(databaseError: DatabaseError) {
                super.onError(databaseError)
                result?.onError()
            }
        }

        recyclerViewComments!!.adapter = adapterComments
        refreshLayoutComments!!.setOnRefreshListener { (adapterComments as FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>).refresh() }
    }

    // 아이템 설정
    private fun setBoard(board: Board) {
        val date = Date(UtilBase64Cipher.decode(board.time).toLong())
        val format = SimpleDateFormat("MM/dd HH:mm", Locale.KOREA)

        _boardForm.value = BoardFormState(
            setId = "by. " + UtilBase64Cipher.decode(board.id),
            setTitle = UtilBase64Cipher.decode(board.title),
            setContents = UtilBase64Cipher.decode(board.contents),
            setTime = format.format(date)
        )
    }

    // 제목 공백 체크
    private fun checkTitle() {
        if (title.isNullOrEmpty()) {
            _boardForm.value = BoardFormState(titleError = R.string.message_isBlank)
            return

        } else
            stateTitle = true
    }

    // 내용 공백 체크
    private fun checkContents() {
        if (contents.isNullOrEmpty()) {
            _boardForm.value = BoardFormState(contentsError = R.string.message_isBlank)
            return

        } else
            stateContents = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}