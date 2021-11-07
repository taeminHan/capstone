package com.cap.withsullivan


import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cap.withsullivan.databinding.ActivityMapBinding
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView

class MapActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    private val TAG = "Test"

    private var tMapView: TMapView? = null
    private var tMapGPS: TMapGpsManager? = null
    private var tMapPoint: TMapPoint? = null

    private var destLat = 0.0
    private var destLon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tMapView = TMapView(this)
        tMapView!!.setSKTMapApiKey("l7xx184eefe2e110491b9ff59f533d66b17b")
        binding.linearLayoutTmap.addView(tMapView)

        // 선택한 목적지 위도 경도 가져오기 나중에 편의점 위치 데이터 불러오면 됨
        destLat = 37.651244
        destLon = 126.670430

        TmapOn()

    }

    // 보행자 경로 그리기
    private fun drawPedestrianPath() {
        val point1 = tMapView!!.centerPoint // 현재위치
        val point2 = TMapPoint(destLat, destLon)
        val tMapData = TMapData()

        tMapData.findPathDataWithType(
            TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2) {
                tMapPolyLine -> tMapPolyLine.lineColor = Color.BLUE
            tMapView!!.addTMapPath(tMapPolyLine)
        }
    }

    private fun TmapOn() {

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

    override fun onLocationChange(location: Location) {
        tMapPoint = tMapGPS!!.location
        var lat = tMapPoint!!.latitude
        var lng = tMapPoint!!.longitude

        try {
            tMapView!!.setCenterPoint(lng, lat)
            tMapView!!.setLocationPoint(lng, lat)

            drawPedestrianPath()
        } catch (e: Exception) {

        }
    }
}
