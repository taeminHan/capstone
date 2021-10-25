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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 음성 인식 결과값 반환
        if (requestCode == RQ_SOEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            Log.d(TAG, result?.get(0).toString())
            VoiceText = result?.get(0).toString()
            if (resultCode == RESULT_OK) {
                text_networking("http:/192.168.1.105:5000/text", VoiceText)
            }
        }
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
    private fun CameraChecked() {
        val intent = Intent(applicationContext, Camera::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = externalCacheDir
            val file = File.createTempFile("photo_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            Camera.cameracheck = 2
            startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
            photoFile = file
        }
    }
}

