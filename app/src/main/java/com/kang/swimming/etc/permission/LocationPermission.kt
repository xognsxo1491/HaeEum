package com.kang.swimming.etc.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// 퍼미션 설정
object LocationPermission {
    fun requestPermission(activity: Activity, context: Context) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse =  ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("권한 거절")
                    .setMessage("권한을 승인해야 해당 게시판의 기능 사용이 원활합니다.").setCancelable(false)
                    .setPositiveButton("확인") { _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                Uri.parse("package:${context.packageName}"))
                            context.startActivity(intent)

                        } catch (e: ActivityNotFoundException) {
                            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                            context.startActivity(intent)
                        }
                    }.setNegativeButton("취소") { _, _ ->  }.show()
            } else
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }

        if (coarse != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                //
            } else
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 2000)
        }
    }
}