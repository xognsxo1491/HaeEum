package com.example.swimming.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.swimming.data.map.MapRepository
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// 지도 관련 뷰모델
class MapViewModel(val repository: MapRepository) : ViewModel() {
    private val disposables = CompositeDisposable()

    private val _mapForm = MutableLiveData<MapFormStatus>()
    val mapFormStatus: LiveData<MapFormStatus> = _mapForm

    // 동네 수족관 위치 불러오기
    fun showPosition(path1: String, path2: String, map: GoogleMap, option: MarkerOptions) {
        val show = repository.showPosition(path1, path2)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ emitter ->
                option.title(UtilBase64Cipher.decode(emitter.store)).snippet(emitter.uuid).position(LatLng(emitter.latitude, emitter.longitude))
                map.addMarker(option)
                map.setOnMarkerClickListener {
                    boardInfo(it.snippet)
                    true
                }
            }, {
                _mapForm.value = MapFormStatus(error = "error")
            })

        disposables.add(show)
    }

    // 게시글 불러오기
    private fun boardInfo(uuid: String) {
        val info = repository.boardInfo("StoreBoard", "StoreBoardInfo", uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { _mapForm.value = MapFormStatus(board = it) },
                { _mapForm.value = MapFormStatus(error = "error") })

        disposables.add(info)
    }

    // 내가 쓴 글
    fun myLike(path1: String, path2: String, path3: String) {
        val load = repository.myLike(path1, path2, path3)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _mapForm.value = MapFormStatus(like = it)
            },{})

        disposables.add(load)
    }
}