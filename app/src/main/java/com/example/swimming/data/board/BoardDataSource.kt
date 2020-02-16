package com.example.swimming.data.board

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedList
import com.example.swimming.R
import com.example.swimming.utils.PostViewHolder
import com.example.swimming.utils.UtilBase64Cipher
import com.example.swimming.utils.UtilTimeFormat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.squareup.picasso.Picasso
import io.reactivex.Completable
import java.text.SimpleDateFormat
import java.util.*

class BoardDataSource {

    // 게시글 작성하기
    fun write(title: String, contents: String, context: Context, path: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path)
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val pref = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
                val userId = pref.getString("Id", "")
                var uuid = System.currentTimeMillis().toString() + UUID.randomUUID().toString()

                val board = Board(UtilBase64Cipher.encode(userId.toString()), title, contents, UtilBase64Cipher.encode(System.currentTimeMillis().toString()), uuid)
                database.child(uuid).setValue(board)
                it.onComplete()
            }
        })
    }

    // 게시글 불러오기
    fun load(owner: LifecycleOwner, path: String) : DatabasePagingOptions<Board> {
        val database = FirebaseDatabase.getInstance().reference.child(path)

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(40) // 초기 개수
            .setEnablePlaceholders(false)
            .setPrefetchDistance(5) // 몇개 남았을 때 불러올지
            .setPageSize(20) // 불러올 개수
            .build()

        return DatabasePagingOptions.Builder<Board>()
            .setLifecycleOwner(owner)
            .setQuery(database, config, Board::class.java)
            .build()
    }

    // 게시글 정보
    fun loadInfo(id: TextView, title: TextView, contents: TextView, time: TextView, profile: ImageView, path: String, child: String) = Completable.create {
        val database = FirebaseDatabase.getInstance().reference.child(path).child(child)
        var board: Board? = null
        database.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                it.onError(p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                board = p0.getValue(Board::class.java)

                id.text = UtilBase64Cipher.decode(board!!.id)
                title.text = UtilBase64Cipher.decode(board!!.title)
                contents.text = UtilBase64Cipher.decode(board!!.contents)

                val data =  Date(UtilBase64Cipher.decode(board!!.time).toLong())
                val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.KOREA)
                val bTime = formatter.format(data)
                time.text = bTime

                val storage = FirebaseStorage.getInstance().getReference("Profile/${id.text}").downloadUrl
                storage.addOnSuccessListener { uri ->
                    Picasso.get().load(uri).into(profile)

                }
            }
        })
    }
}