package com.example.swimming.ui.board

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.data.board.BoardRepository
import com.example.swimming.data.board.Comments
import com.example.swimming.ui.result.Result
import com.example.swimming.adapter.BoardViewHolder
import com.example.swimming.adapter.CommentViewHolder
import com.example.swimming.adapter.DictionaryViewHolder
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class BoardViewModel(val repository: BoardRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _boardForm = MutableLiveData<BoardFormStatus>()
    val boardFormStatus: LiveData<BoardFormStatus> = _boardForm

    private var adapterComments: FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>? = null

    private var statusTitle: Boolean = false
    private var statusContents: Boolean = false

    var recyclerView: RecyclerView? = null
    var refreshLayout: SwipeRefreshLayout? = null

    var result: Result? = null
    var title: String? = null
    var contents: String? = null
    var comments: String? = null

    var linearLayout : LinearLayout? = null
    var data: Intent? = null

    // 게시글 이미지
    var img1 : ImageView? = null
    var img2 : ImageView? = null
    var img3 : ImageView? = null
    var img4 : ImageView? = null
    var img5 : ImageView? = null
    var imgLike : ImageView? = null

    // 게시글 이미지 카드뷰
    var card1 : CardView? = null
    var card2 : CardView? = null
    var card3 : CardView? = null
    var card4 : CardView? = null
    var card5 : CardView? = null

    // 대댓글 관련
    var cardComment: CardView? = null
    var cardCommentComment: CardView? = null
    var textCommentComment: TextView? = null
    var commentComment: String? = null

    var textView: TextView? = null

    // 게시글 작성하기
    fun writeBoard(path1: String, path2: String) {
        checkTitle()
        checkContents()

        if (statusTitle && statusContents) {
            _boardForm.value = BoardFormStatus(loading = R.string.loading)

            if (card1!!.visibility != View.VISIBLE) {
                val write = repository.writeBoard(title!!, contents!!, String.format("0"), path1, path2)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result?.onSuccess() }, { result?.onError() })

                disposables.add(write)
            }

            if (card1!!.visibility == View.VISIBLE) {

                // 업로드할 이미지 여러개
                if (data!!.clipData != null) {
                    for (i in 1..data!!.clipData!!.itemCount) {
                        val uploadImage = repository.uploadImage("$path1/","${data!!.clipData!!.itemCount}", data)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe()

                        val write = repository.writeBoard(title!!, contents!!, data!!.clipData!!.itemCount.toString(), path1, path2)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result?.onSuccess() }, { result?.onError() })

                        disposables.add(write)
                        disposables.add(uploadImage)
                    }

                } else {
                    // 업로드 할 이미지가 1개
                    val uploadImage = repository.uploadImage("$path1/","1", data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe()

                    val write = repository.writeBoard(title!!, contents!!, String.format("1"), path1, path2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result?.onSuccess() }, { result?.onError() })

                    disposables.add(write)
                    disposables.add(uploadImage)
                }
            }
        }
    }

    // 게시글 작성 (수족관 게시판)
    fun writeBoard2(path1: String, path2: String, store: String, latitude: Double, longitude: Double) {
        checkTitle()
        checkContents()

        if (statusTitle && statusContents) {
            _boardForm.value = BoardFormStatus(loading = R.string.loading)

            if (card1!!.visibility != View.VISIBLE) {
                val write = repository.writeBoard2(title!!, contents!!, String.format("0"), path1, path2, store, latitude, longitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result?.onSuccess() }, { result?.onError() })

                disposables.add(write)
            }

            if (card1!!.visibility == View.VISIBLE) {

                // 업로드할 이미지 여러개
                if (data!!.clipData != null) {
                    for (i in 1..data!!.clipData!!.itemCount) {
                        val uploadImage = repository.uploadImage("$path1/","${data!!.clipData!!.itemCount}", data)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.newThread())
                            .subscribe()

                        val write = repository.writeBoard2(title!!, contents!!, data!!.clipData!!.itemCount.toString(), path1, path2, store, latitude, longitude)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result?.onSuccess() }, { result?.onError() })

                        disposables.add(write)
                        disposables.add(uploadImage)
                    }

                } else {
                    // 업로드 할 이미지가 1개
                    val uploadImage = repository.uploadImage("$path1/","1", data)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe()

                    val write = repository.writeBoard2(title!!, contents!!, String.format("1"), path1, path2, store, latitude, longitude)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result?.onSuccess() }, { result?.onError() })

                    disposables.add(write)
                    disposables.add(uploadImage)
                }
            }
        }
    }

    // 게시글 삭제
    fun deleteBoard(path1: String, path2: String, path3: String, uuid: String, count: String) {
        val delete = repository.deleteBoard(path1, path2, path3, uuid, count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(delete)
    }

    // 게시글 삭제 조회
    fun checkBoard(path1: String, path2: String, uuid: String) {
        val check = repository.checkBoard(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(check = R.string.message_delete_board)
            }, {})

        disposables.add(check)
    }

    // 게시글 갤러리 이미지 선택
    fun setImage() {
        if (data!!.clipData != null) {

            if (data!!.clipData!!.itemCount > 5) {
                result?.onFailed()

            } else {
                for (i in 0 until data!!.clipData!!.itemCount) {
                    val cards = arrayOf(card1, card2, card3, card4, card5)
                    val images = arrayOf(img1, img2, img3,img4, img5)

                    cards[i]!!.visibility = View.VISIBLE
                    images[i]!!.setImageURI(data!!.clipData!!.getItemAt(i).uri)
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
                    _boardForm.value = BoardFormStatus(
                        image0 = it.split("이미지0: ")[1]
                    )
                }

                if (it.contains("이미지1: ")) {
                    _boardForm.value = BoardFormStatus(
                        image1 = it.split("이미지1: ")[1]
                    )
                }

                if (it.contains("이미지2: ")) {
                    _boardForm.value = BoardFormStatus(
                        image2 = it.split("이미지2: ")[1]
                    )
                }

                if (it.contains("이미지3: ")) {
                    _boardForm.value = BoardFormStatus(
                        image3 = it.split("이미지3: ")[1]
                    )
                }

                if (it.contains("이미지4: ")) {
                    _boardForm.value = BoardFormStatus(
                        image4 = it.split("이미지4: ")[1]
                    )
                }

                if (it.contains("이미지5: ")) {
                    _boardForm.value = BoardFormStatus(
                        image5 = it.split("이미지5: ")[1]
                    )
                }

            }, {
                _boardForm.value = BoardFormStatus( error = R.string.message_image)
            })

        disposables.add(load)
    }

    // 게시글 불러오기
    fun loadBoardList(owner: LifecycleOwner, path1: String, path2: String) {
        val option = repository.loadBoardList(owner, path1, path2)
        var context: Context? = null

        val adapter = object : FirebaseRecyclerPagingAdapter<Board, BoardViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
                context = parent.context
                return BoardViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))
            }

            override fun onBindViewHolder(p0: BoardViewHolder, p1: Int, p2: Board) {
                p0.setItem(p2)

                if (path1 == "StoreBoard")
                    p0.onClick2(p0.itemView, context!!, p2, path1)
                else
                    p0.onClick(p0.itemView, context!!, p2, path1)

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

    // 게시글 불러오기 (물고기 사전)
    fun loadBoardList2(owner: LifecycleOwner, path1: String, path2: String) {
        val option = repository.loadBoardList(owner, path1, path2)
        var context: Context? = null

        val adapter = object : FirebaseRecyclerPagingAdapter<Board, DictionaryViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
                context = parent.context
                return DictionaryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_dictionary, parent, false))
            }

            override fun onBindViewHolder(p0: DictionaryViewHolder, p1: Int, p2: Board) {
                p0.setItem(p2, context!!, p2.uuid)
                p0.onClick(p0.itemView, context!!, p2, path1)
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

    // 댓글 작성
    fun uploadComments(path1: String, path2: String, uuid: String) {
            val upload = repository.uploadComments(path1, path2, uuid, comments!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { adapterComments!!.refresh() }

            disposables.add(upload)
    }

    // 대댓글 작성
    fun uploadCommentsComments(path1: String, path2: String, uuid: String, key: String, comments: String) {
        val upload = repository.uploadCommentsComments(path1, path2, uuid, key, comments)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { adapterComments!!.refresh() }

        disposables.add(upload)
    }

    // 댓글 삭제
    fun deleteComments(path1: String, path2: String, path3: String, uuid: String) {
        val delete = repository.deleteComments(path1, path2, path3, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(delete)
    }

    // 댓글 불러오기
    fun loadComments(owner: LifecycleOwner, path1: String, path2: String, path3: String, uuid: String) {
        val option = repository.loadComments(owner, path1, path2, uuid)
        var context: Context? = null

        adapterComments = object : FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
                context = parent.context
                return CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false))
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(p0: CommentViewHolder, p1: Int, p2: Comments) {
                p0.setItem(p2)
                p0.itemView.setOnClickListener {
                    if (repository.id == UtilBase64Cipher.decode(p2.id) || repository.id == String.format("Admin1")) {
                        val items = arrayOf("삭제하기", "대댓글 작성")
                        val alert = AlertDialog.Builder(context)
                            alert.setItems(items) { _, w ->
                            when (items[w]) {
                                "삭제하기" -> {
                                    deleteComments(path1, path2, uuid, p2.uuid)
                                    updateCommentCountMinus(path1, path3, uuid)
                                    refresh()
                                    textView!!.text = (Integer.parseInt(textView!!.text.toString()) - 1).toString()
                                }
                                "대댓글 작성" -> {
                                    cardComment!!.visibility = View.INVISIBLE
                                    cardCommentComment!!.visibility = View.VISIBLE
                                    textCommentComment!!.text = "@${UtilBase64Cipher.decode(p2.id)}"
                                    commentComment = p2.uuid
                                }
                            }
                        }.show()

                    } else {
                        val items = arrayOf("대댓글 작성")
                        val alert = AlertDialog.Builder(context)
                        alert.setItems(items) { _, w ->
                            when (items[w]) {
                                "대댓글 작성" -> {
                                    cardComment!!.visibility = View.INVISIBLE
                                    cardCommentComment!!.visibility = View.VISIBLE
                                    textCommentComment!!.text = "@${UtilBase64Cipher.decode(p2.id)}"
                                    commentComment = p2.uuid
                                }
                            }
                        }.show()
                    }
                }
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
        refreshLayout!!.setOnRefreshListener {
            (adapterComments as FirebaseRecyclerPagingAdapter<Comments, CommentViewHolder>).refresh()

            // 새로고침 시 댓글, 하트수 불러오기
            loadCommentCount(path1, path3, uuid)
            loadBoardLike(path1, path3, uuid)
        }
    }

    // 댓글 개수 불러오기
    private fun loadCommentCount(path1: String, path2: String, uuid: String) {
        val load = repository.loadCommentCount(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                _boardForm.value = BoardFormStatus(
                    setCommentCount = UtilBase64Cipher.decode(it)
                )
            }, {})

        disposables.add(load)
    }

    // 댓글 개수 업데이트 (플러스)
    fun updateCommentCountPlus(path1: String, path2: String, uuid: String) {
            val load = repository.updateCommentCountPlus(path1, path2, uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

            disposables.add(load)
    }

    // 댓글 개수 업데이트 (마이너스)
    fun updateCommentCountMinus(path1: String, path2: String, uuid: String) {
        val load = repository.updateCommentCountMinus(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(load)
    }

    // 좋아요
    fun uploadBoardLike(path1: String, path2: String, uuid: String) {
        val upload = repository.uploadBoardLike(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(messageLike = R.string.like)
            },{})

        disposables.add(upload)
    }

    // 좋아요 취소
    fun deleteBoardLike(path1: String, path2: String, uuid: String) {
        val delete = repository.deleteBoardLike(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {})

        disposables.add(delete)
    }

    // 좋아요 상태 불러오기
    fun checkBoardLike(path1: String, path2: String, uuid: String) {
        val check = repository.checkBoardLike(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                imgLike!!.tag = R.string.like
                imgLike!!.setImageResource(R.drawable.round_favorite_24)
            },{})

        disposables.add(check)
    }

    // 좋아요 개수 불러오기
    fun loadBoardLike(path1: String, path2: String, uuid: String) {
        val load = repository.loadBoardLike(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(
                    setLikeCount = UtilBase64Cipher.decode(it)
                )}, {}
            )

        disposables.add(load)
    }

    // 좋아요 개수 업데이트 (플러스)
    fun updateBoardLikeCountPlus(path1: String, path2: String, uuid: String) {
        val update = repository.updateBoardLikeCount(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(update)
    }

    // 좋아요 개수 업데이트 (마이너스)
    fun updateBoardLikeCountMinus(path1: String, path2: String, uuid: String) {
        val update = repository.updateBoardLikeCountMinus(path1, path2, uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(update)
    }

    // 키워드 검색
    fun searchKeyword(path1: String, path2: String, keyword: String) {
        val load = repository.searchKeyword(path1, path2, keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(board = it)
            }, {})

        disposables.add(load)
    }

    // 내가 쓴 글
    fun myBoard(path1: String, path2: String) {
        val load = repository.myBoard(path1, path2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(board = it)
            },{})

        disposables.add(load)
    }


    // 내가 쓴 댓글
    fun myComments(path1: String, path2: String, path3: String) {
        val load = repository.myComments(path1, path2, path3)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(board = it)
            },{})

        disposables.add(load)
    }

    // 내가 누른 좋아요
    fun myLike(path1: String, path2: String, path3: String) {
        val load = repository.myLike(path1, path2, path3)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _boardForm.value = BoardFormStatus(board = it)
            },{})

        disposables.add(load)
    }

    // 토큰
    fun pushToken(title: String, contents: String, token: String, fcm: String, key: String) {
        val push = repository.pushToken(title, contents, token, fcm, key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(push)
    }

    // 알림 메세지 전달
    fun pushMessage(path1: String, path2: String, uuid: String, kind: String, title: String, contents: String) {
        val push = repository.pushMessage(path1, path2, uuid, (9999999999999 - System.currentTimeMillis()).toString() + UUID.randomUUID().toString(), kind, title, contents)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        disposables.add(push)
    }

    // 제목 공백 체크
    private fun checkTitle() {
        if (title.isNullOrEmpty()) {
            _boardForm.value = BoardFormStatus(titleError = R.string.message_isBlank)
            return

        } else
            statusTitle = true
    }

    // 내용 공백 체크
    private fun checkContents() {
        if (contents.isNullOrEmpty()) {
            _boardForm.value = BoardFormStatus(contentsError = R.string.message_isBlank)
            return

        } else
            statusContents = true
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}