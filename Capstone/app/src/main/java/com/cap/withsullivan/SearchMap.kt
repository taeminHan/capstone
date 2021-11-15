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

        binding.Select1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 0
            Toast.makeText(this, scanname_list[choice], Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

        binding.Select2.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 1
            Toast.makeText(this, scanname_list[choice], Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

        binding.Select3.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 2
            Toast.makeText(this, scanname_list[choice], Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

        binding.Select4.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            choice = 3
            Toast.makeText(this, scanname_list[choice], Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

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
                            "Point: " + item.poiPoint.toString() +", Poiid: " +item.poiid
                )
            }
            binding.Text1.setText(poiItem[0].poiName.toString())
            binding.Text2.setText(poiItem[1].poiName.toString())
            binding.Text3.setText(poiItem[2].poiName.toString())
            binding.Text4.setText(poiItem[3].poiName.toString())

            fun Choice_list() {
                for (i: Int in 0..3) {
                    latitude_list[i] = poiItem[i].poiPoint.latitude
                    longitude_list[i] = poiItem[i].poiPoint.longitude
                    convenience_list[i] = poiItem[i].poiName
                }
            }
            Choice_list()
            Read_convenience()
        }
    }
    // 길안내
//    fun doload(){
//        val tMapTapi = TMapTapi(this)
//        tMapTapi.invokeNavigate(m_mapPoint.get(0).name,
//            m_mapPoint.get(0).longitude.toFloat(), m_mapPoint.get(0).latitude.toFloat(), 0, true)
//        CameraChecked()
//    }

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


