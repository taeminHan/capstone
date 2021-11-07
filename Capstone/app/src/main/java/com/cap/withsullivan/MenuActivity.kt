package com.cap.withsullivan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cap.withsullivan.databinding.ActivityMenuBinding
import kotlinx.android.synthetic.main.activity_menu.*
import java.io.File
import java.lang.Exception


private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100

class MenuActivity : AppCompatActivity() {

    // 사진 파일
    private lateinit var photoFile: File
    private val TAG = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val test = 123
        val intent2 = Intent(this, SearchMap::class.java)
        intent2.putExtra("Test", test)

        // 카메라 권한 사용
        checkPermission()

        // 위치 권한 사용
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        binding.mapbutton.setOnClickListener{
            val intent = Intent(this, SearchMap::class.java)
            startActivity(intent)
        }

        binding.detectmodebutton.setOnClickListener{
            CameraChecked()
        }

        binding.drinkModeButton.setOnClickListener {
            val intent = Intent(this, Voicesearch::class.java)
            startActivity(intent)
        }
    }

    private fun CameraChecked() {
        try {
            val intent = Intent(applicationContext, DetectActivity::class.java)
            if (intent.resolveActivity(packageManager) != null) {
                val dir = externalCacheDir
                val file = File.createTempFile("photo_", ".jpg", dir)
                val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//            Camera.cameracheck = 1
                startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
                photoFile = file
                Log.d(TAG, "상품 인식 성공")
            }
        } catch (e: Exception) {
            Log.d(TAG, "${e.message}")
        }
    }

    fun checkPermission() {
        val cameraPermission = ContextCompat.checkSelfPermission(
            this@MenuActivity,
            Manifest.permission.CAMERA
        )
        if(cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우 permission 권한을 띄우는 알람창을 띄운다.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1000)
        }
    }
}
