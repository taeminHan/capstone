package com.cap.withsullivan

//import kotlinx.android.synthetic.main.activity_findmap.*
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.cap.withsullivan.databinding.ActivitySearchMapBinding
import com.skt.Tmap.*
import com.skt.Tmap.TMapData.*
import com.skt.Tmap.TMapView.OnCalloutRightButtonClickCallback
import kotlinx.android.synthetic.main.activity_search_map.*
import java.io.File
import java.util.*

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

// 편의점 위치 리스트
var latitude_list = mutableListOf(1.0, 2.0, 3.0, 4.0)
var longitude_list = mutableListOf(1.0, 2.0, 3.0, 4.0)

// 버튼 클릭 후 내 위치
var latitude_start = 0.0
var longitude_start = 0.0

// 편의점 이름 인식
var scanname_list = mutableListOf("11", "11", "11", "11")
var convenience_list = mutableListOf("1", "1", "1", "1")

var choice: Int = 0

class SearchMap : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    val binding by lazy { ActivitySearchMapBinding.inflate(layoutInflater) }

    private var mContext: Context? = null
    private val m_bTrackingMode = true

    private var tmapdata: TMapData? = null
    private var tmapgps: TMapGpsManager? = null
    private var tmapview: TMapView? = null
    private val mApiKey = "l7xx56b5c5d3d31e4677a16ae492e680f8e7"

    private val TAG = "Test"

    private lateinit var photoFile: File

    override fun onLocationChange(location: Location) {
        tmapview!!.setLocationPoint(location.longitude, location.latitude)
        tmapview!!.setCenterPoint(location.longitude, location.latitude)

        latitude_start = location.latitude
        longitude_start = location.longitude

        Log.d(TAG, "$latitude_start, $longitude_start 현재위치")

        getAroundBizPoi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mContext = this

        //Tmap 각종 객체 선언
        tmapdata = TMapData() //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
        tmapview = TMapView(this)
        tmapview!!.setSKTMapApiKey(mApiKey)

        /* 언어 설정 */
        tmapview!!.setLanguage(TMapView.LANGUAGE_KOREAN)
        tmapgps = TMapGpsManager(this@SearchMap) //단말의 위치탐색을 위한 클래스
        tmapgps!!.setMinTime(1000) //위치변경 인식 최소시간설정
        tmapgps!!.setMinDistance(5f) //위치변경 인식 최소거리설정
        tmapgps!!.setProvider(TMapGpsManager.NETWORK_PROVIDER) //네트워크 기반의 위치탐색
        tmapgps!!.OpenGps()

        binding.Text1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 0
            toast(scanname_list[choice])
            startActivity(intent)
            finish()
        }

        binding.Text2.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 1
            toast(scanname_list[choice])
            startActivity(intent)
            finish()
        }

        binding.Text3.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 2
            toast(scanname_list[choice])
            startActivity(intent)
            finish()
        }

        binding.Text4.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 3
            toast(scanname_list[choice])
            startActivity(intent)
            finish()
        }
    }

    // 편의점 종류 인식
    private fun Read_convenience() {
        for (i: Int in 0..3) {
            scanname_list[i] = convenience_list[i][0].toString() + convenience_list[i][1].toString()
            if (scanname_list[i] == "세븐") {
                scanname_list[i] = "세븐일레븐"
            } else if (scanname_list[i] == "GS") {
                scanname_list[i] = "GS25"
            } else if (scanname_list[i] == "이마") {
                scanname_list[i] = "이마트24"
            } else if (scanname_list[i] == "미니") {
                scanname_list[i] = "미니스톱"
            } else if (scanname_list[i] == "CU") {
                scanname_list[i] = "CU"
            }
        }
        Log.d(TAG, "$scanname_list")
    }

    //2. 주변 편의시설 검색하기
    private fun getAroundBizPoi() {

        // 검색중 화면 제거
        binding.Loading.visibility = View.INVISIBLE

        // 검색 후 버튼 보이기
        binding.Select1.visibility = View.VISIBLE
        binding.Select2.visibility = View.VISIBLE
        binding.Select3.visibility = View.VISIBLE
        binding.Select4.visibility = View.VISIBLE
        binding.Text1.visibility = View.VISIBLE
        binding.Text2.visibility = View.VISIBLE
        binding.Text3.visibility = View.VISIBLE
        binding.Text4.visibility = View.VISIBLE

        val tmapdata = TMapData()
        val point = tmapview!!.centerPoint
        tmapdata.findAroundNamePOI(
            point, "편의점", 1, 10
        ) { poiItem ->
            for (i in poiItem.indices) {
                val item = poiItem[i]
                Log.d(
                    TAG, "POI Name: " + item.poiName.toString() + ", " +
                            "Address: " + item.poiAddress.replace("null", "") + ", " +
                            "Point: " + item.poiPoint.toString() + ", Poiid: " + item.poiid
                )
            }

            // 검색 결과 끝에 "주차장" 결과값 제거
            var count = 0
            while (true) {
                if (poiItem[count].poiName.toString().endsWith("주차장")) {
                    count += 1
                } else {
                    binding.Text1.setText(poiItem[count].poiName.toString())
                    latitude_list[0] = poiItem[count].poiPoint.latitude
                    longitude_list[0] = poiItem[count].poiPoint.longitude
                    convenience_list[0] = poiItem[count].poiName
                    break
                }
            }
            count += 1
            while (true) {
                if (poiItem[count].poiName.toString().endsWith("주차장")) {
                    count += 1
                } else {
                    binding.Text2.setText(poiItem[count].poiName.toString())
                    latitude_list[1] = poiItem[count].poiPoint.latitude
                    longitude_list[1] = poiItem[count].poiPoint.longitude
                    convenience_list[1] = poiItem[count].poiName
                    break
                }
            }
            count += 1
            while (true) {
                if (poiItem[count].poiName.toString().endsWith("주차장")) {
                    count += 1
                } else {
                    binding.Text3.setText(poiItem[count].poiName.toString())
                    latitude_list[2] = poiItem[count].poiPoint.latitude
                    longitude_list[2] = poiItem[count].poiPoint.longitude
                    convenience_list[2] = poiItem[count].poiName
                    break
                }
            }
            count += 1
            while (true) {
                if (poiItem[count].poiName.toString().endsWith("주차장")) {
                    count += 1
                } else {
                    binding.Text4.setText(poiItem[count].poiName.toString())
                    latitude_list[3] = poiItem[count].poiPoint.latitude
                    longitude_list[3] = poiItem[count].poiPoint.longitude
                    convenience_list[3] = poiItem[count].poiName
                    break
                }
            }
            Read_convenience()
        }
    }
    // toast 함수
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


