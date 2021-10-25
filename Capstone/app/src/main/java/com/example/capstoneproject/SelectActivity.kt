package com.example.capstoneproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.capstoneproject.databinding.ActivitySelectBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL


private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100
const val TAG = "GOOGLE_SIGN_IN_TAG"


class SelectActivity : AppCompatActivity() {
    // 뷰 바인딩
    private lateinit var binding: ActivitySelectBinding

    // firebase 인증
    private lateinit var firebaseAuth: FirebaseAuth

    // 사진 파일
    private lateinit var photoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        // 바인딩 활성화
        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 파이어베이스 활성화
        firebaseAuth = FirebaseAuth.getInstance()
        //checkUser() 구글 유저 체크 보류

        // 뒤로가기 버튼
        binding.back.setOnClickListener {
            firebaseAuth.signOut()
            //checkUser() 구글 유저 체크 보류
            BackActivity()
        }

        // 지도 버튼 활성화
        binding.SearchMap.setOnClickListener {
            onLoginButtonClicked()
        }

        // 카메라 버튼 활성화
        binding.SearchCamera.setOnClickListener {
            CameraChecked()
        }

        binding.DrinkCamera.setOnClickListener {
            DrinkChecked()
        }

    }

    // 음성으로 음료 검색하기
    private fun DrinkChecked() {
        Log.d(TAG, "SelectActivity - DrinkChecked() called")
        val intent = Intent(this, DrinkCamera::class.java)
        startActivity(intent)
    }

    // 뒤로가기 버튼 구현
    private fun BackActivity() {
        Log.d(TAG, "SelectActivity - BackActivity() called")
        finish()
    }

    // 카메라 구현
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

    // 유저 체크
    private fun checkUser() {
        // 현재 사용자 정보
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            // 로그인 상태가 아닐때
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // 유저 로그인 상태
            val email = firebaseUser.email
            Log.d(TAG, "SelectActivity - $email")
        }
    }

    // 지도버튼 구현
    private fun onLoginButtonClicked() {
        try {
            Log.d(TAG, "SelectActivity - onLoginButtonClicked() called")
            val intent = Intent(this, SearchMap::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "SelectActivity - onLoginButtonClicked: ${e.message}")

        }

    }

}