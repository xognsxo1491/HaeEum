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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
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

    var recyclerView: RecyclerView? = null
    var refreshLayout: SwipeRefreshLayout? = null

    var result: Result? = null
    var title: String? = null
    var contents: String? = null
    var comment: TextView? = null

    private var adapterComments: FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>? = null
    var linearLayout : LinearLayout? = null
    var data: Intent? = null

    var img0 : ImageView? = null
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
    fun writeBoard(path1: String, path2: String) {

        checkTitle()
        checkContents()

        if (stateTitle && stateContents) {
            _boardForm.value = BoardFormState(loading = R.string.loading)

            if (card1!!.visibility != View.VISIBLE) {

                val write = repository.writeBoard(title!!, contents!!, String.format("0"), String.format("0"), path1, path2)
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

                            val write = repository.writeBoard(title!!, contents!!, String.format("2"), String.format("0"), path1, path2)
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

                            val write = repository.writeBoard(title!!, contents!!, String.format("3"), String.format("0"), path1, path2)
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

                            val write = repository.writeBoard(title!!, contents!!, String.format("4"), String.format("0"), path1, path2)
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

                            val write = repository.writeBoard(title!!, contents!!, String.format("5"), String.format("0"), path1, path2)
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

                    val write = repository.writeBoard(title!!, contents!!, String.format("1"), String.format("0"), path1, path2)
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

    // 이미지 불러오기
    fun loadImage(path: String, count: String) {

        val load = repository.loadImage(path, count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.contains("이미지0: ")) {
                    Glide.with(context).load((it.split("이미지0: ")[1])).into(img0!!).waitForLayout()
                    img0!!.visibility = View.VISIBLE
                }

                if (it.contains("이미지1: ")) {
                    card1!!.visibility = View.VISIBLE
                    Glide.with(context).load((it.split("이미지1: ")[1])).into(img1!!).waitForLayout()
                }

                if (it.contains("이미지2: ")) {
                    card2!!.visibility = View.VISIBLE
                    Glide.with(context).load((it.split("이미지2: ")[1])).into(img2!!).waitForLayout()
                }

                if (it.contains("이미지3: ")) {
                    card3!!.visibility = View.VISIBLE
                    Glide.with(context).load((it.split("이미지3: ")[1])).into(img3!!).waitForLayout()
                }

                if (it.contains("이미지4: ")) {
                    card4!!.visibility = View.VISIBLE
                    Glide.with(context).load((it.split("이미지4: ")[1])).into(img4!!).waitForLayout()
                }

                if (it.contains("이미지5: ")) {
                    card5!!.visibility = View.VISIBLE
                    Glide.with(context).load((it.split("이미지5: ")[1])).into(img5!!).waitForLayout()
                }

            }, {})

        disposables.add(load)
    }

    // 게시글 불러오기
    fun loadBoardList(owner: LifecycleOwner, path1: String, path2: String) {
        val option = repository.loadBoardList(owner, path1, path2)

        val adapter = object : FirebaseRecyclerPagingAdapter<Board, BoardViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
                return BoardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))
            }

            override fun onBindViewHolder(p0: BoardViewHolder, p1: Int, p2: Board) {
                p0.setItem(p2)
                p0.onClick(p0.itemView, context, path1)
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

    // 게시글 내용 불러오기
    fun infoBoard(path1: String, path2: String, child: String)  {
        val loadInfo = repository.infoBoard(path1, path2, child)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { t -> setBoard(t) }
            .doOnError { t: Throwable? -> throw t!! }
            .subscribe()

        disposables.add(loadInfo)
    }

    // 댓글 작성
    fun uploadComments(path1: String, path2: String, child: String, contents: String) {
        val upload = repository.uploadComments(path1, path2, child, contents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {adapterComments!!.refresh()}

        disposables.add(upload)
    }

    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, child: String) {
        val option = repository.loadComments(owner, path1, path2, child)

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
                        refreshLayout!!.isRefreshing = false
                    }

                    LoadingState.LOADING_MORE -> {
                        refreshLayout!!.isRefreshing = false
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

        recyclerView!!.setHasFixedSize(false)
        recyclerView!!.adapter = adapterComments
        refreshLayout!!.setOnRefreshListener { (adapterComments as FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>).refresh() }
    }

    fun loadCommentCount(path1: String, path2: String, uuid: String) {
        val load = repository.loadCommentCount(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                comment!!.text = UtilBase64Cipher.decode(it)

            }, {})

        disposables.add(load)
    }

    fun updateCommentCount(path1: String, path2: String, uuid: String, num: String) {
        val load = repository.updateCommentCount(path1, path2, uuid, num)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(load)
    }

    // 아이템 설정
    private fun setBoard(board: Board) {
        val date = Date(UtilBase64Cipher.decode(board.time).toLong())
        val format = SimpleDateFormat("MM/dd HH:mm", Locale.KOREA)

        _boardForm.value = BoardFormState(
            setId = "by. " + UtilBase64Cipher.decode(board.id),
            setTitle = UtilBase64Cipher.decode(board.title),
            setContents = UtilBase64Cipher.decode(board.contents),
            setTime = format.format(date),
            setImgCount = UtilBase64Cipher.decode(board.imgCount)
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