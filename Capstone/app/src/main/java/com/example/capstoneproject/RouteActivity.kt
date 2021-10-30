package com.example.capstoneproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.capstoneproject.databinding.ActivityRouteBinding
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapData.FindPathDataListenerCallback
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapPolyLine
import com.skt.Tmap.TMapView
import kotlinx.coroutines.delay
import java.io.File

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

class RouteActivity : AppCompatActivity() {
    private lateinit var photoFile: File
    private lateinit var binding: ActivityRouteBinding
    val TAG = "GOOGLE_SIGN_IN_TAG"

    var tMapView: TMapView? = null
    var mainActivity: MainActivity? = null

    private var destLat = 0.0
    private var destLon = 0.0
    var latitude:kotlin.Double = 0.0
    var longitude:kotlin.Double = 0.0
    var lm: LocationManager? = null
    var temp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tMapView = TMapView(this)

        val tbTracking = findViewById<View>(R.id.toggleButton) as ToggleButton
        binding.routeLayoutTmap.addView(tMapView)

        // 기본 줌 세팅
        tMapView!!.setIconVisibility(true)
        tMapView!!.zoomLevel = 17

        // 선택한 목적지 위도 경도 가져오기 나중에 편의점 위치 데이터 불러오면 됨
        intent = intent
        destLat = 37.651244
        destLon = 126.670430

        // 출발지 목적지 경유지 같은 아이콘으로 설정
        val tMapData = TMapData()
        val start = BitmapFactory.decodeResource(resources, R.drawable.poi_star)
        val end = BitmapFactory.decodeResource(resources, R.drawable.poi_star)
        val pass = BitmapFactory.decodeResource(resources, R.drawable.poi_star)
        tMapView!!.setTMapPathIcon(start, end, pass)

        // 현재 위치로 표시
        setGps()
        // 트레킹모드 활성화 여부, 버튼으로 동작
        tbTracking.setOnClickListener {
            if (tbTracking.isChecked)
                setTracking(true)
            else
                setTracking(
                false
            )
        }

        val handler = Handler()
        handler.postDelayed({
            tMapView!!.setCenterPoint(longitude, latitude)
            drawPedestrianPath()
        }, 3000)

        binding.back.setOnClickListener {
            BackActivity()
        }
    }

    private fun BackActivity() {
        finish()
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

    private fun setGps() {
        lm = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        lm!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, mLocationListener)
        lm!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, mLocationListener)
    }

    private val mLocationListener = LocationListener { location ->
            latitude = location.latitude
            longitude = location.longitude
            Log.d(TAG, "위치 : $latitude , $longitude")
            tMapView!!.setLocationPoint(longitude, latitude)
    }

    // 토글버튼 클릭시 트래킹모드 설정여부
    fun setTracking(toggle: Boolean) {
        if (toggle == true) { // 트래킹 모드 실행되면 현재위치로 중심점 옮김
            setGps()
            tMapView!!.setCenterPoint(longitude, latitude)
            Toast.makeText(applicationContext, "트래킹모드 활성화", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "트래킹모드 비활성화", Toast.LENGTH_SHORT).show()
        }

        Log.d(TAG, toggle.toString())
        tMapView!!.setCompassMode(toggle)
        tMapView!!.setSightVisible(toggle)
        tMapView!!.setTrackingMode(toggle)
        tMapView!!.zoomLevel = 17
    }
    private fun CameraChecked() {
        val intent = Intent(applicationContext, com.example.capstoneproject.Camera::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = externalCacheDir
            val file = File.createTempFile("photo_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            Camera.cameracheck = 1
            startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
            photoFile = file
        }
    }
}