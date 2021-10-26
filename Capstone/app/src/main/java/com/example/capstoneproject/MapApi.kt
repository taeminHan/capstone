package com.example.capstoneproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstoneproject.databinding.ActivityMapBinding
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.lang.Exception
import kotlin.system.exitProcess

class MapApi : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    private lateinit var binding: ActivityMapBinding
    var tMapView: TMapView? = null
    var tMapGPS: TMapGpsManager? = null
    var tMapPoint: TMapPoint? = null
    val TAG = "GOOGLE_SIGN_IN_TAG"

    private val MyLocation: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tMapView = TMapView(this)
        tMapView!!.setSKTMapApiKey("l7xx184eefe2e110491b9ff59f533d66b17b")
        binding.linearLayoutTmap.addView(tMapView)

        TmapOn()

        // 뒤로가기 버튼
        binding.back.setOnClickListener {
            BackActivity()
        }

        binding.RouteActivity.setOnClickListener {
            RouteActivity()
        }

    }

    private fun RouteActivity() {
        val intent = Intent(this, RouteActivity::class.java)
        startActivity(intent)
    }

    private fun TmapOn() {
        // 위치 권한 불러오기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // 지도 줌 설정
        tMapView!!.zoomLevel = 17
        tMapView!!.setIconVisibility(true)
        tMapView!!.mapType = TMapView.MAPTYPE_STANDARD
        tMapView!!.setLanguage(TMapView.LANGUAGE_KOREAN)

        // 내 위치 찍기
        tMapView!!.setIconVisibility(true)

        tMapGPS = TMapGpsManager(this)

        // 간격마다 위치값 가져오기
        tMapGPS!!.minTime = 1000
        tMapGPS!!.minDistance = 5f
        tMapGPS!!.provider = TMapGpsManager.NETWORK_PROVIDER

        tMapGPS!!.setLocationCallback()
        tMapGPS!!.OpenGps()
    }

    private fun BackActivity() {
        finish()
    }

    override fun onLocationChange(location: Location) {
        tMapPoint = tMapGPS!!.location
        var lat = tMapPoint!!.latitude
        var lng = tMapPoint!!.longitude

        Log.d(TAG, tMapPoint.toString())
        Log.d(TAG, "$lat, $lng")
        try {
            tMapView!!.setCenterPoint(lng, lat)
            tMapView!!.setLocationPoint(lng, lat)
            Log.d(TAG, "위치 찍기 성공")
        } catch (e: Exception) {
            Log.d(TAG, "위치 찍기 실패")
        }

        // 현재 위치로 이동
        binding.MyLocation.setOnClickListener {
            Log.d(TAG, tMapPoint.toString())
            try {
                tMapView!!.zoomLevel = 17
                tMapView!!.setCenterPoint(lng, lat)
                tMapView!!.setLocationPoint(lng, lat)
                Log.d(TAG, "성공")
            } catch (e: Exception) {
                Log.d(TAG, "실패 : " + e.message)
            }
        }

    }

}
