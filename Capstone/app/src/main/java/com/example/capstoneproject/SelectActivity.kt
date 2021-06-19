package com.example.capstoneproject

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.capstoneproject.databinding.ActivitySelectBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100
private const val TAG = "GOOGLE_SIGN_IN_TAG"

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
    }

    // 뒤로가기 버튼 구현
    private fun BackActivity() {
        Log.d(TAG, "SelectActivity - BackActivity() called")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // 카메라 구현
    private fun CameraChecked() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = externalCacheDir
            val file = File.createTempFile("photo_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
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
    private fun img_networking(urlString: String, file: File) {
        // URL을 만들어 주고
        val url = URL(urlString)

        //데이터를 담아 보낼 바디를 만든다
//        val requestBody : RequestBody = FormBody.Builder()
//            .add("id","아이디")
//            .build()
        // OkHttp Request 를 만들어준다.
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "photo",
                "photo.png",
                RequestBody.create(MediaType.parse("image/jpg"), file)
            )
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 클라이언트 생성
        val client = OkHttpClient()

        Log.d("전송 주소 ",urlString)

        // 요청 전송
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                Log.d("요청","요청 완료")
                Log.d("파일 이름:", photoFile.name)
                var text = response.toString()
                Log.d("리스톤: ", text)
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청","요청 실패 ")
            }


        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_FOR_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
//                    BitmapFactory.decodeFile(photoFile.absolutePath)?.let {
//                        binding.image.setImageBitmap(it) }
//                    Glide.with(this).load(photoFile).into(binding.image)
//                    Log.d(TAG, "$photoFile")
                    img_networking("ec2-3-36-99-241.ap-northeast-2.compute.amazonaws.com", photoFile)
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
