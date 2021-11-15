package com.cap.withsullivan

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
import com.cap.withsullivan.databinding.ActivityVoicesearchBinding
import okhttp3.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import okhttp3.RequestBody
import org.json.JSONObject
import java.lang.Exception

private const val REQUEST_CODE_FOR_IMAGE_CAPTURE = 100
// 음성텍스트 저장
private var VoiceText = ""

class Voicesearch : AppCompatActivity() {

    private val RQ_SOEECH_REC = 102
    private lateinit var photoFile: File
    private val TAG = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityVoicesearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.Voice.setOnClickListener {
            askSpeechInput()
        }
    }

    // 음성 인식
    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "음성 인식을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
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

        // 음성 인식 결과값 변환
        if (requestCode == RQ_SOEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            VoiceText = result?.get(0).toString()
            if (resultCode == RESULT_OK) {
                text_networking("http:/121.162.15.236:80/text", VoiceText)
            }
        }
    }

    private fun text_networking(urlString: String, text: String) {
        val url = URL(urlString)
        val client = OkHttpClient()
        val requestBody: RequestBody
        val body = FormBody.Builder()
            .add("beveragename", text)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@Voicesearch, "서버 요청 성공", Toast.LENGTH_SHORT).show()
                    }
                    val resStr = response.body!!.string()
                    val json = JSONObject(resStr)

                    val obj = json.getString("name")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(applicationContext, obj, Toast.LENGTH_SHORT).show()
                    }
                    CameraChecked()
                } catch (e: Exception) {
                    Log.d(TAG, "${e.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                try {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(this@Voicesearch, "서버 요청 실패", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "${e.message}")
                }
            }

        })
    }
    private fun CameraChecked() {
        val intent = Intent(applicationContext, DetectActivity::class.java)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = externalCacheDir
            val file = File.createTempFile("photo_", ".jpg", dir)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            DetectActivity.cameracheck = 2
            startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_CAPTURE)
            photoFile = file
        }
    }
}