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

class SearchMap : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {

    val binding by lazy { ActivitySearchMapBinding.inflate(layoutInflater) }

    private var mContext: Context? = null
    private val m_bTrackingMode = true

    private var tmapdata: TMapData? = null
    private var tmapgps: TMapGpsManager? = null
    private var tmapview: TMapView? = null
    private val mApiKey = "l7xx56b5c5d3d31e4677a16ae492e680f8e7"
    private var mMarkerID = 0

    private val m_tmapPoint = ArrayList<TMapPoint>()
    private val mArrayMarkerID = ArrayList<String>()
    private val m_mapPoint: ArrayList<MapPoint> = ArrayList<MapPoint>()

    private val TAG = "Test"

    private var address: String? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var photoFile: File


    override fun onLocationChange(location: Location) {
        tmapview!!.setLocationPoint(location.getLongitude(), location.getLatitude())
        tmapview!!.setCenterPoint(location.getLongitude(), location.getLatitude())

        getAroundBizPoi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val testL = intent.getStringExtra("Test")
        Log.d(TAG, "$testL")

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

        val handler = Handler()
        handler.postDelayed(Runnable {
//            getAroundBizPoi()
        }, 3000)

        binding.Select1.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
//            finish()
        }

        binding.Select2.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.Select3.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.Select4.setOnClickListener {
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
            finish()
        }

//        do_tmap.setOnClickListener { doload() }
    }

    fun showMarkerPoint() {
//        for (i in 0 until m_mapPoint.size) {
//            val point = TMapPoint(
//                m_mapPoint.get(i).latitude,
//                m_mapPoint.get(i).longitude
//            )
//        }

    }

    //2. 주변 편의시설 검색하기
    fun getAroundBizPoi() {

        binding.Loading.visibility = View.INVISIBLE

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
//                m_mapPoint.add(
//                    MapPoint(item.poiName,item.poiPoint.latitude,item.poiPoint.longitude)
//                )
            }
            binding.Text1.setText(poiItem[0].poiName.toString())
            binding.Text2.setText(poiItem[1].poiName.toString())
            binding.Text3.setText(poiItem[2].poiName.toString())
            binding.Text4.setText(poiItem[3].poiName.toString())
            Log.d(TAG, poiItem[3].poiPoint.toString())
            val nextIntent = Intent(this, MapActivity::class.java)
            nextIntent.putExtra("Test1", poiItem[3].poiPoint.toString())
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


