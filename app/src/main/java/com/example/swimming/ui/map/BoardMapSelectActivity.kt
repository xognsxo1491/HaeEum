package com.example.swimming.ui.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.swimming.R
import com.example.swimming.databinding.ActivityBoardMapSelectBinding
import com.example.swimming.ui.board.BoardWriteActivity
import com.example.swimming.utils.PermissionLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// 수족관 위치 선택
class BoardMapSelectActivity : AppCompatActivity(), KodeinAware, OnMapReadyCallback {
    override val kodein by kodein()
    private val factory: MapViewModelFactory by instance()
    private lateinit var mBinding: ActivityBoardMapSelectBinding

    private val key = "AIzaSyC-x0hbrLoMnWp607rYLIMzuKQP7QL0PKs"
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mMap: GoogleMap
    private lateinit var mOption: MarkerOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_board_map_select)
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
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mOption = MarkerOptions()

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val seoul = LatLng(37.52487, 126.92723)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 18f))
            mMap.setOnMapClickListener {
                mOption.position(LatLng(it.latitude, it.longitude))
                mMap.addMarker(mOption)
                mBinding.textRegion.text = "수족관 위치의 마커를 클릭해주세요."
            }

            mMap.setOnMarkerClickListener{
                val dialog = AlertDialog.Builder(this)
                val edit = EditText(this)
                dialog.setMessage("수족관 이름을 입력해주세요.")
                    .setView(edit)
                    .setPositiveButton("확인"){_, _ ->
                        val intent = Intent(this, BoardWriteActivity::class.java)
                        intent.putExtra("latitude", mOption.position.latitude)
                        intent.putExtra("longitude", mOption.position.longitude)
                        intent.putExtra("BoardKind","StoreBoard")
                        intent.putExtra("storeName", edit.text.toString())
                        startActivity(intent)

                }.setNegativeButton("취소"){_,_ -> }.show()
                true
            }
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

    override fun onResume() {
        super.onResume()
        mBinding.mapView.onResume()
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    override fun onStart() {
        super.onStart()
        mBinding.mapView.onStart()

        if (Build.VERSION.SDK_INT >= 23)
            PermissionLocation.requestMapPermissions(this)
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
                    mMap.setOnMapClickListener {
                        mOption.position(LatLng(it.latitude, it.longitude))
                        mMap.addMarker(mOption)
                        mBinding.textRegion.text = "수족관 위치의 마커를 클릭해주세요."
                    }

                    mMap.setOnMarkerClickListener{
                        val dialog = AlertDialog.Builder(this@BoardMapSelectActivity)
                        val edit = EditText(this@BoardMapSelectActivity)
                        runOnUiThread {
                            dialog.setMessage("수족관 이름을 입력해주세요.")
                                .setView(edit)
                                .setPositiveButton("확인"){_, _ ->
                                    val intent = Intent(this@BoardMapSelectActivity, BoardWriteActivity::class.java)
                                    intent.putExtra("latitude", mOption.position.latitude)
                                    intent.putExtra("longitude", mOption.position.longitude)
                                    intent.putExtra("BoardKind","StoreBoard")
                                    intent.putExtra("storeName", edit.text.toString())
                                    startActivity(intent)

                                }.setNegativeButton("취소"){_,_ -> }.show()
                        }
                        true
                    }
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
