package com.cap.withsullivan

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.cap.withsullivan.databinding.ActivityDetectBinding
import kotlinx.android.synthetic.main.activity_detect.*

class DetectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detect)

        val binding = ActivityDetectBinding.inflate(layoutInflater);
        setContentView(binding.root);

        val REQUEST_IMAGE_CAPTURE = 1

//        fun dispatchTakePictureIntent() {
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//                takePictureIntent.resolveActivity(packageManager)?.also {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//
//
//
//            dispatchTakePictureIntent()
        }
    }