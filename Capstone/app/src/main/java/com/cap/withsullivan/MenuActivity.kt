package com.cap.withsullivan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cap.withsullivan.databinding.ActivityMenuBinding
import kotlinx.android.synthetic.main.activity_menu.*
import java.io.File
import java.lang.Exception
import android.location.LocationManager
import android.provider.Settings
import android.view.View

import android.content.DialogInterface
import android.content.IntentSender.SendIntentException
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse


private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

class MenuActivity : AppCompatActivity() {

    // 사진 파일
    private lateinit var photoFile: File
    private val TAG = "Test"
    var locationManager: LocationManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카메라 권한 사용
        checkPermission()

        // 위치 권한 사용
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        binding.mapbutton.setOnClickListener {
            if (!checkLocationService()) {
                toast("GPS가 꺼져 있습니다.")
                gps()
            } else {
                val intent = Intent(this, SearchMap::class.java)
                startActivity(intent)
            }
        }

        binding.detectmodebutton.setOnClickListener {
            CameraChecked()
        }

        binding.drinkModeButton.setOnClickListener {
            val intent = Intent(this, Voicesearch::class.java)
            startActivity(intent)
        }
    }

    // 카메라 이동
    private fun CameraChecked() {
        try {
            val intent = Intent(applicationContext, DetectActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                val dir = externalCacheDir
                val file = File.createTempFile("photo_", ".jpg", dir)
                val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                DetectActivity.cameracheck = 1
                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
                photoFile = file
                Log.d(TAG, "상품 인식 성공")
            }
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    // 카메라 권한
    fun checkPermission() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this@MenuActivity,
            Manifest.permission.CAMERA
        )
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 permission 권한을 띄우는 알람창을 띄운다.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1000)
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // toast 함수
    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // GPS가 켜져 있는지 확인
    fun gps() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this) { }
        task.addOnFailureListener(this) { e: Exception ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this@MenuActivity, 0x1)
                } catch (sendEx: SendIntentException) {
                    Log.d("TAG", e.message!!)
                }
            }
        }
    }

}
