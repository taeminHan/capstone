package com.example.capstoneproject

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.tmap.*
import android.location.Location
import androidx.core.content.ContextCompat.startActivity
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback
import kotlinx.android.synthetic.main.activity_findmap.*
import kotlinx.android.synthetic.main.main.*


class FindTmap : AppCompatActivity(), onLocationChangedCallback {
    var API_Key = "l7xx56b5c5d3d31e4677a16ae492e680f8e7"

    // T Map View
    var tMapView: TMapView? = null

    // T Map GPS
    var tMapGPS: TMapGpsManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findmap)

        // T Map View
        tMapView = TMapView(this)

        // API Key
        tMapView!!.setSKTMapApiKey(API_Key)

        // Initial Setting
        tMapView!!.zoomLevel = 17
        tMapView!!.setIconVisibility(true)
        tMapView!!.mapType = TMapView.MAPTYPE_STANDARD
        tMapView!!.setLanguage(TMapView.LANGUAGE_KOREAN)

        // T Map View Using Linear Layout
        val linearLayoutTmap = findViewById<View>(R.id.linearLayoutTmap) as LinearLayout
        linearLayoutTmap.addView(tMapView)

        // Request For GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // GPS using T Map
        tMapGPS = TMapGpsManager(this)

        // Initial Setting
        tMapGPS!!.minTime = 1000
        tMapGPS!!.minDistance = 10f
        tMapGPS!!.provider = TMapGpsManager.NETWORK_PROVIDER
        //tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);
        tMapGPS!!.OpenGps()
        Find.setOnClickListener { RouteActivity() }
    }

    override fun onLocationChange(location: Location) {
        tMapView!!.setLocationPoint(location.getLongitude(), location.getLatitude())
        tMapView!!.setCenterPoint(location.getLongitude(), location.getLatitude())
    }

    private fun RouteActivity() {
            val intent = Intent(this, Tmap::class.java)
            startActivity(intent)
    }
}

