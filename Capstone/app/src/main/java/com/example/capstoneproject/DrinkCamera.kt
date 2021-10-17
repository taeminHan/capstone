package com.example.capstoneproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.capstoneproject.databinding.ActivityDrinkCameraBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import okhttp3.RequestBody
import okhttp3.OkHttpClient



private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100
// 음성텍스트 저장
private var VoiceText = ""

class DrinkCamera : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkCameraBinding
    private val TAG = "GOOGLE_SIGN_IN_TAG"
    private val RQ_SOEECH_REC = 102
    private lateinit var photoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_camera)

        binding = ActivityDrinkCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            BackActivity()
        }

        binding.VoiceTest.setOnClickListener {
            askSpeechInput()
        }

        binding.ServerTest.setOnClickListener {
            CameraChecked()
        }
    }

    // 뒤로가기
    private fun BackActivity() {
        Log.d(TAG, "BackActivity: 뒤로가기")
        finish()
    }

    // 음성 인식
    private fun askSpeechInput() {

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "원하는 음료를 말씀하세요.")
            startActivityForResult(i, RQ_SOEECH_REC)
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 음성 인식 결과값 반환
        if (requestCode == RQ_SOEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.result.text = result?.get(0).toString()
            Log.d(TAG, result?.get(0).toString())
            VoiceText = result?.get(0).toString()
            if (resultCode == RESULT_OK) {
                text_networking("http:/ec2-3-35-54-213.ap-northeast-2.compute.amazonaws.com:5000/text", VoiceText)
            }
        }
        // 사진 찍은 후 결과값
        when (requestCode) {
            REQUEST_CODE_FOR_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
//                    BitmapFactory.decodeFile(photoFile.absolutePath)?.let {
//                        binding.image.setImageBitmap(it) }
//                    Glide.with(this).load(photoFile).into(binding.image)
//                    Log.d(TAG, "$photoFile")
                    img_networking("http:/ec2-3-35-54-213.ap-northeast-2.compute.amazonaws.com:5000/img", photoFile)
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
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
                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            )
            .build()
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()

        Log.d(TAG, "전송 주소 $urlString")
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "요청 완료")
                Log.d(TAG, "파일 이름: ${photoFile.name}")

//                val result: String = Gson().toJson(response.body()!!.string())
//                Log.d("JSON", result)
                val resStr = response.body!!.string()
                val json = JSONObject(resStr)

                val obj = json.getString("object")
                val price = json.getString("price")
                val facts = json.getString("nutrition_facts")
                val event = json.getString("event")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(applicationContext, obj, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, price, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, facts, Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, event, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "서버 요청 실패 ")
            }
        })
    }

    private fun text_networking(urlString: String, text: String) {
        val url = URL(urlString)
        val client = OkHttpClient()
        val requestBody: RequestBody
        val body = FormBody.Builder()
            .add("beveragename", text)
            .build();
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build();

        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "요청 완료")
                CameraChecked()
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "서버 요청 실패 ")
            }

        })
    }
}

