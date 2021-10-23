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

    // 서버 전송
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
                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            )
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()

        Log.d(TAG,"전송 주소 $urlString")
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG,"요청 완료")
                Log.d(TAG,"파일 이름: ${photoFile.name}")

//                val result: String = Gson().toJson(response.body()!!.string())
//                Log.d("JSON", result)
                val resStr = response.body!!.string()
                val json = JSONObject(resStr)

                val obj = json.getString("object")
                val price = json.getString("price")
                val facts = json.getString("nutrition_facts")
                val event = json.getString("event")
                Handler(Looper.getMainLooper()).post{
                    Toast.makeText(applicationContext, obj,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, price,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, facts,Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, event,Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG,"서버 요청 실패 ")
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
                    img_networking("http:/ec2-3-35-54-213.ap-northeast-2.compute.amazonaws.com:5000/imgInformation", photoFile)
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}