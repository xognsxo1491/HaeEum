package com.example.swimming.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swimming.R
import com.example.swimming.data.board.Board
import com.example.swimming.databinding.ActivityBoardMapBinding
import com.example.swimming.adapter.LikeAdapter
import com.example.swimming.ui.board.BoardInfoMapActivity
import com.example.swimming.etc.permission.LocationPermission
import com.example.swimming.etc.utils.UtilBase64Cipher
import com.example.swimming.etc.utils.UtilTimeFormat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 우리 동네 수족관
class BoardMapActivity : AppCompatActivity(), KodeinAware, OnMapReadyCallback {
    override val kodein by kodein()
    private val factory: MapViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardMapBinding
    private lateinit var viewModel: MapViewModel

    private val key = "AIzaSyC-x0hbrLoMnWp607rYLIMzuKQP7QL0PKs"
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mMap: GoogleMap
    private lateinit var mLayout: SlidingUpPanelLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_map)
        mBinding.viewModel = viewModel

        setSupportActionBar(mBinding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_chevron_left_24)

        var bundle: Bundle? = null
        if (savedInstanceState != null) {
            bundle = savedInstanceState.getBundle(key)
        }
        getLocationUpdate()

        mBinding.mapView.onCreate(bundle)
        mBinding.mapView.getMapAsync(this)
        mBinding.imageButton.setOnClickListener {
            checkLocation()
        }

        val list = ArrayList<Board>()
        mLayout = mBinding.slide

        viewModel.myLike("StoreBoard", "StoreBoardLike", "StoreBoardInfo")
        viewModel.mapFormStatus.observe(this, Observer {
            val status = it ?: return@Observer

            if (status.board != null) {
                mBinding.include.text_board_id.text = UtilBase64Cipher.decode(status.board!!.id)
                text_board_title.text = UtilBase64Cipher.decode(status.board!!.title)
                text_board_contents.text = UtilBase64Cipher.decode(status.board!!.contents).replace(" ", "\u00A0")
                text_board_time.text = (UtilTimeFormat.formatting((UtilBase64Cipher.decode(status.board!!.time).toLong())))
                text_board_imgCount.text = UtilBase64Cipher.decode(status.board!!.imgCount)
                text_board_commentCount.text = UtilBase64Cipher.decode(status.board!!.commentCount)
                text_board_like.text = UtilBase64Cipher.decode(status.board!!.like)

                mBinding.include.visibility = View.VISIBLE
                mBinding.textView.visibility = View.VISIBLE
                mBinding.layout.visibility = View.GONE

                mLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }

            if (status.like != null) {
                mBinding.textLikePosition.visibility = View.GONE
                list.add(status.like!!)
            }

            mBinding.recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mBinding.recycler.adapter = LikeAdapter(list, mMap)
            mBinding.include.setOnClickListener {
                val intent = Intent(this, BoardInfoMapActivity::class.java)
                intent.putExtra("BoardKind", "StoreBoard")
                intent.putExtra("uuid", status.board!!.uuid)
                intent.putExtra("id", UtilBase64Cipher.decode(status.board!!.id))
                intent.putExtra("title", UtilBase64Cipher.decode(status.board!!.title))
                intent.putExtra("contents", UtilBase64Cipher.decode(status.board!!.contents))
                intent.putExtra("time", UtilBase64Cipher.decode(status.board!!.time))
                intent.putExtra("imgCount", UtilBase64Cipher.decode(status.board!!.imgCount))
                intent.putExtra("comment", UtilBase64Cipher.decode(status.board!!.commentCount))
                intent.putExtra("like", UtilBase64Cipher.decode(status.board!!.like))
                intent.putExtra("token", status.board!!.token)
                intent.putExtra("store", UtilBase64Cipher.decode(status.board!!.store))
                intent.putExtra("latitude", status.board!!.latitude)
                intent.putExtra("longitude", status.board!!.longitude)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            if (status.error != null) {
                Toast.makeText(this, R.string.message_error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val option = MarkerOptions()

        viewModel.showPosition("StoreBoard", "StoreBoardInfo", p0, option)

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val seoul = LatLng(37.52487, 126.92723)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 18f))

        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        var bundle = outState.getBundle(key)
        if (bundle == null) {
            bundle = Bundle()
            outState.putBundle(key, bundle)
        }
    }

    override fun onBackPressed() {
        if (mLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED) {
            mLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

            mBinding.include.visibility = View.GONE
            mBinding.textView.visibility = View.GONE
            mBinding.layout.visibility = View.VISIBLE

        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()

        if (Build.VERSION.SDK_INT >= 23)
            LocationPermission.requestMapPermissions(this)
    }

    override fun onStop() {
        super.onStop()
        mBinding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mBinding.mapView.onPause()
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.mapView.onDestroy()
        mBinding.unbind()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mBinding.mapView.onLowMemory()
    }

    // gps 설정 확인
    private fun checkLocation() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("GPS 설정을 켜야 해당 기능을 사용할 수 있습니다.")
                .setPositiveButton("설정") {_, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("취소") {_, _ -> }.show()
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }

    // 현재 위치 구하기
    private fun getLocationUpdate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 50000
        mLocationRequest.fastestInterval = 50000
        mLocationRequest.smallestDisplacement = 170f
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0 ?: return
                if (p0.locations.isNotEmpty()) {

                    val now = LatLng(p0.lastLocation.latitude, p0.lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now, 13f))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
