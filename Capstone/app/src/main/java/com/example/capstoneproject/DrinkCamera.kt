package com.example.capstoneproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import com.example.capstoneproject.databinding.ActivityDrinkCameraBinding
import java.util.*

class DrinkCamera : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkCameraBinding
    private val TAG = "GOOGLE_SIGN_IN_TAG"
    private val RQ_SOEECH_REC = 102

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

        }
    }

    private fun BackActivity() {
        Log.d(TAG, "BackActivity: 뒤로가기")
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SOEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.result.text = result?.get(0).toString()
        }
        val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        Log.d(TAG, "onActivityResult: $result")
    }

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
}
