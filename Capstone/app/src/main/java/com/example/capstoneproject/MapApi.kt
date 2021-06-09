package com.example.capstoneproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneproject.databinding.ActivityMapBinding

class MapApi : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    private companion object {
        private const val TAG = "로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            BackActivity()
        }
    }

    private fun BackActivity() {
        Log.d(MapApi.TAG, "MapApi - BackActivity() called")

        val intent = Intent(this, SearchMap::class.java)
        startActivity(intent)
    }
}