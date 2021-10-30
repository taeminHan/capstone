package com.example.capstoneproject

import android.R.attr
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapData
import com.skt.Tmap.TMapMarkerItem
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapData.*
import com.skt.Tmap.TMapView.OnCalloutRightButtonClickCallback
import com.skt.Tmap.poi_item.TMapPOIItem
import kotlinx.android.synthetic.main.activity_findmap.*
import kotlinx.android.synthetic.main.tmap.*
import android.R.attr.bitmap
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.skt.Tmap.TMapTapi
import java.io.File

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

class Tmap : AppCompatActivity(), TMapGpsManager.onLocationChangedCallback {
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



    private var address: String? = null
    private var lat: Double? = null
    private var lon: Double? = null
    private lateinit var photoFile: File


    override fun onLocationChange(location: Location) {
        if (m_bTrackingMode) {
            tmapview!!.setLocationPoint(location.longitude, location.latitude)
        }
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tmap)
        mContext = this

        //Tmap 각종 객체 선언
        tmapdata = TMapData() //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
        val linearLayout = findViewById(R.id.mapview) as LinearLayout
        tmapview = TMapView(this)
        linearLayout.addView(tmapview)
        tmapview!!.setSKTMapApiKey(mApiKey)


        /* 현재 보는 방향 */
        tmapview!!.setCompassMode(true)

        /* 현위치 아이콘표시 */
        tmapview!!.setIconVisibility(true)

        /* 줌레벨 */
        tmapview!!.setZoomLevel(15)

        /* 지도 타입 */
        tmapview!!.setMapType(TMapView.MAPTYPE_STANDARD)

        /* 언어 설정 */
        tmapview!!.setLanguage(TMapView.LANGUAGE_KOREAN)
        tmapgps = TMapGpsManager(this@Tmap) //단말의 위치탐색을 위한 클래스
        tmapgps!!.setMinTime(1000) //위치변경 인식 최소시간설정
        tmapgps!!.setMinDistance(5f) //위치변경 인식 최소거리설정
        tmapgps!!.setProvider(TMapGpsManager.NETWORK_PROVIDER) //네트워크 기반의 위치탐색
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //위성기반의 위치탐색
        tmapgps!!.OpenGps()

        /*  화면중심을 단말의 현재위치로 이동 */
        tmapview!!.setTrackingMode(true)
        tmapview!!.setSightVisible(true)


        //풍선 클릭시
        tmapview!!.setOnCalloutRightButtonClickListener(OnCalloutRightButtonClickCallback { markerItem ->
            lat = markerItem.latitude
            lon = markerItem.longitude

            //1. 위도, 경도로 주소 검색하기
            tmapdata!!.convertGpsToAddress(lat!!,
                lon!!, ConvertGPSToAddressListenerCallback { strAddress -> address = strAddress })
            Toast.makeText(this@Tmap, "주소 : $address", Toast.LENGTH_SHORT).show()
        })
        bt_findadd.setOnClickListener { showMarkerPoint() }
        bt_findfac.setOnClickListener { getAroundBizPoi() }
        do_tmap.setOnClickListener { doload() }
    }


    private fun addMarker(lat: Double, lng: Double) {
        val lat = lat
        val lng = lng
        val point = TMapPoint(lat, lng)
        val marker = TMapMarkerItem()
        marker.tMapPoint = point
        tmapview!!.addMarkerItem("marker", marker)
    }

    fun showMarkerPoint() {
        for (i in 0 until m_mapPoint.size) {
            val point = TMapPoint(
                m_mapPoint.get(i).latitude,
                m_mapPoint.get(i).longitude
            )
            val item1 = TMapMarkerItem()
            var bitmap: Bitmap? = null
            /* 핀 이미지 */
            bitmap = BitmapFactory.decodeResource(mContext!!.resources, R.drawable.poi_star)
            item1.tMapPoint = point
            item1.name = m_mapPoint.get(i).name
            item1.visible = TMapMarkerItem.VISIBLE
            item1.icon = bitmap

            item1.setPosition(0.5f, 1.0f);
            item1.setTMapPoint(item1.tMapPoint);
            item1.setName(item1.name);
            tmapview!!.setCenterPoint(point.latitude, point.longitude);
            tmapview!!.addMarkerItem("markerItem1" + i, item1);

        }

    }


    //3. 주소검색으로 위도, 경도 검색하기
    /* 명칭 검색을 통한 주소 변환 */
    fun convertToAddress() {
        //다이얼로그 띄워서, 검색창에 입력받음
        val builder = AlertDialog.Builder(this)
        builder.setTitle("POI 통합 검색")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("확인") { dialog, which ->
            val strData = input.text.toString()
            val tmapdata = TMapData()
            tmapdata.findAllPOI(strData) { poiItem ->
                for (i in poiItem.indices) {
                    val item = poiItem[i]
                    Log.d(
                        "주소로찾기", "POI Name: " + item.poiName.toString() + ", " +
                                "Address: " + item.poiAddress.replace("null", "") + ", " +
                                "Point: " + item.poiPoint.toString()
                    )
                }
            }
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    //2. 주변 편의시설 검색하기
    /* 화면 중심의 위도 경도를 통한, 주변 편의시설 검색 */
    fun getAroundBizPoi() {
        val tmapdata = TMapData()
        val point = tmapview!!.centerPoint
        tmapdata.findAroundNamePOI(
            point, "편의점", 1, 10
        ) { poiItem ->
            for (i in poiItem.indices) {
                val item = poiItem[i]
                Log.d(
                    "주소로찾기", "POI Name: " + item.poiName.toString() + ", " +
                            "Address: " + item.poiAddress.replace("null", "") + ", " +
                            "Point: " + item.poiPoint.toString() +", Poiid: " +item.poiid
                )
                m_mapPoint.add(
                    MapPoint(item.poiName,item.poiPoint.latitude,item.poiPoint.longitude)
                )
            }

        }
    }
    fun doload(){
        val tMapTapi = TMapTapi(this)
        tMapTapi.invokeNavigate(m_mapPoint.get(0).name,
            m_mapPoint.get(0).longitude.toFloat(), m_mapPoint.get(0).latitude.toFloat(), 0, true)

        CameraChecked()

    }
    // 카메라 구현
    private fun CameraChecked() {
        val intent = Intent(applicationContext, Camera::class.java)
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


