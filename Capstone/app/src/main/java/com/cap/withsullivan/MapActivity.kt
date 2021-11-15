package com.cap.withsullivan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.cap.withsullivan.databinding.ActivityMapBinding
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import java.io.File
import java.util.*

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

class MapActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val TAG = "Test"
    var tts: TextToSpeech? = null

    private lateinit var photoFile: File
    val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }

    var lm: LocationManager? = null
    var mLocationListener: LocationListener? = null

    var path = ArrayList<String>()
    var path_coor = ArrayList<Double>()

    var spLat = 0.0
    var spLng = 0.0
    var epLat = 0.0
    var epLng = 0.0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 화면 켜짐 유지
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        tts = TextToSpeech(this, this)

        start()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun start() {
        val isOnTrack = true
        val tMapView = TMapView(this)
        tMapView.setSKTMapApiKey("l7xx56b5c5d3d31e4677a16ae492e680f8e7")

        spLat = latitude_start
        spLng = longitude_start
        epLat = latitude_list[choice]
        epLng = longitude_list[choice]

        val tmapdata = TMapData() //경로 데이터를 저장하기 위한 변수
        val tMapPointStart = TMapPoint(spLat, spLng) // 경로의 시작점
        val tMapPointEnd = TMapPoint(epLat, epLng) // 경로의 끝점
        val tmapgps: TMapGpsManager? = null

        //경로를 받아와서 지도에 line으로 그려주는 함수
        tmapdata.findPathDataWithType(
            TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd
        ) { polyLine -> tMapView.addTMapPath(polyLine) }

        // 현재위치로 표시될 아이콘을 표시할지 여부를 설정
        tMapView.setIconVisibility(true)

        tmapdata.findPathDataAllType(
            TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd) { document ->

            // 길 안내
            val root = document.documentElement
            val nodeListPlacemark = root.getElementsByTagName("Placemark")
            for (i in 0 until nodeListPlacemark.length) {
                val nodeListPlacemarkItem = nodeListPlacemark.item(i).childNodes

                for (j in 0 until nodeListPlacemarkItem.length) {
                    if (nodeListPlacemarkItem.item(j).nodeName == "description") {
                        var insert_check = false

                        if (!nodeListPlacemarkItem.item(j).textContent.contains(",")) {
                            insert_check = true
                            Log.d(TAG, nodeListPlacemarkItem.item(j).textContent)
                            path.add(nodeListPlacemarkItem.item(j).textContent)
                        }
                    }
                    if (nodeListPlacemarkItem.item(j).nodeName == "Linestring" || nodeListPlacemarkItem.item(j).nodeName == "Point") {
                        val coordi_node = nodeListPlacemarkItem.item(j).childNodes
                        for (k in 0 until coordi_node.length) {
                            if (coordi_node.item(k).nodeName == "coordinates") {
                                val coor_array = coordi_node.item(k).textContent.split(",").toTypedArray()
                                path_coor.add(coor_array[0].toDouble())
                                path_coor.add(coor_array[1].toDouble())
                            }
                        }
                    }
                }
            }
        }

        // KML파싱함수
        lm = getSystemService(LOCATION_SERVICE) as LocationManager
        mLocationListener = object : LocationListener {
            var nodeCurrent = 0 // 알려주어야 할 노드가 무엇인지 알아봐야 하므로 알려주면 하나씩 증가
            override fun onLocationChanged(location: Location) {
                if (location != null) {
                    val longitude = location.longitude //경도
                    val latitude = location.latitude //위도

                    tMapView.setLocationPoint(longitude, latitude)
                    tMapView.setCenterPoint(longitude, latitude, true)
                    Log.d(TAG, "path size is : " + path.size + " path_coor is : " + path_coor.size + " nodecurrent: " + nodeCurrent)
                    var intent = Intent(applicationContext, MenuActivity::class.java)
                    if (nodeCurrent == 0) {
                        binding.Drawing.visibility = View.INVISIBLE
                        binding.linearLayoutTmap.visibility = View.VISIBLE
                        if (path.size > 0 && path_coor.size > 0) {
                            try {
                                onRead("지금부터 안내를 시작할게요. ${path[nodeCurrent]} 하세요. 직진이에요.")
                                Log.d(TAG, "음성 나오냐?")
                            } catch (e: Exception) {
                                Log.d(TAG, "${e.message} 음성 오류!! ㅈㅈ")
                            }
                            Log.d(TAG, "nodeCurrent: $nodeCurrent")
                            Log.d(TAG, Integer.toString(path_coor.size))
                            nodeCurrent++
                        } else {
                            Log.d("back step required: ", "path is 0 & path_coor is 0")
                        }

                    } else if (2 * nodeCurrent + 2 < path_coor.size) {
                        if (path_coor[2 * nodeCurrent] > longitude - 0.0001 && path_coor[2 * nodeCurrent] < longitude + 0.0001 && path_coor[2 * nodeCurrent + 1] > latitude - 0.0001 && path_coor[2 * nodeCurrent + 1] < latitude + 0.0001) {
                            onRead(path[nodeCurrent] + "하세요.")

                            Log.d(TAG, "nodeCurrent: $nodeCurrent")
                            nodeCurrent++
                        }

                    } else if (2 * nodeCurrent + 2 == path_coor.size) {
                        if (path_coor[2 * nodeCurrent] > longitude - 0.0001 && path_coor[2 * nodeCurrent] < longitude + 0.0001 && path_coor[2 * nodeCurrent + 1] > latitude - 0.0001 && path_coor[2 * nodeCurrent + 1] < latitude + 0.0001) {
                            onRead(path[nodeCurrent] + "하셨어요. 안내를 종료할게요.")

                            Log.d(TAG, "Got it!")
                            Log.d(TAG, "nodeCurrent: $nodeCurrent")
                            nodeCurrent++
                        }

                    } else if (2 * nodeCurrent + 2 > path_coor.size) {
                        toast("메인화면으로 돌아갑니다.")
                        CameraChecked()
                        lm!!.removeUpdates(this)
                        finish()
                    }
                }
            }

            override fun onProviderDisabled(provider: String) {
                // Disabled시
                Log.d(TAG, "onProviderDisabled, provider:$provider")
            }

            override fun onProviderEnabled(provider: String) {
                // Enabled시
                Log.d(TAG, "onProviderEnabled, provider:$provider")
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

            }
        }
        try {
            if (isOnTrack) {
                lm!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 50, 1f, mLocationListener as LocationListener
                )

                lm!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 50, 1f, mLocationListener as LocationListener
                )
            } else {
                lm!!.removeUpdates(mLocationListener as LocationListener) //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }
        } catch (ex: SecurityException) {

        }

        // 현재위치
        val locationProvider = LocationManager.NETWORK_PROVIDER
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = lm!!.getLastKnownLocation(locationProvider)
        toast("현재위치" + location!!.longitude.toString() + "/" + location.latitude.toString())

        // Tmap지도 설정
        //tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true)
        tMapView.zoomLevel = 18
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)
        tMapView.setTrackingMode(true)
        tMapView.setSightVisible(true)
        binding.linearLayoutTmap.addView(tMapView)
    }

    // 뒤로가기 버튼 클릭
    override fun onBackPressed() {
        Toast.makeText(this, "안내를 종료합니다", Toast.LENGTH_SHORT).show()
        lm!!.removeUpdates(mLocationListener!!)
        path.clear()
        path_coor.clear()
        spLat = 0.0
        spLng = 0.0
        epLat = 0.0
        epLng = 0.0
        Log.d(TAG, "Back button in path size is : " + path.size + "path_coor is : " + path_coor.size)
        super.onBackPressed()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    public override fun onDestroy() {
        super.onDestroy()
        lm!!.removeUpdates(mLocationListener!!)
        path.clear()
        path_coor.clear()
        spLat = 0.0
        spLng = 0.0
        epLat = 0.0
        epLng = 0.0
        Log.d(TAG, "Back button in I'm destroyed")
        Log.d(TAG, "Back button in path size is : " + path.size + "path_coor is : " + path_coor.size)
    }

    // tts
    fun onRead(text: String?): String? {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        return text
    }

    // tts 설정
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.KOREAN)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The Language specified is not supported!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Initilization Failed!", Toast.LENGTH_SHORT).show()
        }
    }
    // 카메라 구현
    private fun CameraChecked() {
        val intent = Intent(applicationContext, DetectActivity::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = externalCacheDir
            val file = File.createTempFile("photo_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            DetectActivity.cameracheck = 1
            startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
            photoFile = file
        }
    }
}