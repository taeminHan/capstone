package com.cap.withsullivan


import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cap.withsullivan.databinding.ActivityMapBinding
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView

class MapActivity : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    private val TAG = "Test"

    private var tMapView: TMapView? = null
    private var tMapGPS: TMapGpsManager? = null
    private var tMapPoint: TMapPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 화면 켜짐 유지
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        tMapView = TMapView(this)
        tMapView!!.setSKTMapApiKey("l7xx184eefe2e110491b9ff59f533d66b17b")
        binding.linearLayoutTmap.addView(tMapView)

        TmapOn()

    }

    private fun TmapOn() {

        // 지도 줌 설정
        tMapView!!.zoomLevel = 17
        tMapView!!.setIconVisibility(true)
        tMapView!!.mapType = TMapView.MAPTYPE_STANDARD
        tMapView!!.setLanguage(TMapView.LANGUAGE_KOREAN)

        tMapView!!.setCompassMode(true)
        tMapView!!.setSightVisible(true)
        tMapView!!.setTrackingMode(true)

        // 마커 제거
        val start = BitmapFactory.decodeResource(resources, R.drawable.none)
        val end = BitmapFactory.decodeResource(resources, R.drawable.none)
        val pass = BitmapFactory.decodeResource(resources, R.drawable.none)
        tMapView!!.setTMapPathIcon(start, end, pass)

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

    // 보행자 경로 그리기
    private fun drawPedestrianPath() {
        val point1 = tMapView!!.centerPoint // 현재위치
        for (i: Int in 0..3) {
            if (choice == i) {
                val point2 = TMapPoint(latitude_list[i], longitude_list[i])
                val tMapData = TMapData()

                tMapData.findPathDataWithType(
                    TMapData.TMapPathType.PEDESTRIAN_PATH, point1, point2) {
                        tMapPolyLine -> tMapPolyLine.lineColor = Color.BLUE
                    tMapView!!.addTMapPath(tMapPolyLine)
                }
            }
        }
    }

    override fun onLocationChange(location: Location) {
        tMapPoint = tMapGPS!!.location
        var lat = tMapPoint!!.latitude
        var lng = tMapPoint!!.longitude

        try {
            tMapView!!.setCenterPoint(lng, lat)
            tMapView!!.setLocationPoint(lng, lat)

            drawPedestrianPath()
            binding.Drawing.visibility = View.INVISIBLE
            binding.linearLayoutTmap.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.d(TAG, "GPS를 켜주세요.")
        }
    }
}