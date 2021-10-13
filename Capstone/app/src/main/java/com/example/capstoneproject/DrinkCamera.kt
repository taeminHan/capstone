package com.example.capstoneproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.capstoneproject.databinding.ActivityDrinkCameraBinding
import java.io.File
import java.util.*

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
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "원하는 음료를 말씀하세요.")
            startActivityForResult(i,RQ_SOEECH_REC)
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
        }
        // 사진 찍은 후 결과값
        when(requestCode) {
            REQUEST_CODE_FOR_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
//                    img_networking("http:/ec2-3-35-54-213.ap-northeast-2.compute.amazonaws.com:5000/img")
                } else {
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show()
                    Log.d(TAG, VoiceText)
                }
            }
        }
    }
}
