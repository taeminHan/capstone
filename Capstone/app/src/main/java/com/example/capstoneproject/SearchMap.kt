package com.example.capstoneproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.capstoneproject.databinding.ActivitySearchmapBinding
import java.lang.Exception

class SearchMap : AppCompatActivity() {

    private lateinit var binding: ActivitySearchmapBinding

    private companion object {
        private const val TAG = "로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        binding = ActivitySearchmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.select1.setOnClickListener {
            SelectButton()
        }
        binding.back.setOnClickListener {
            BackActivity()
        }
    }

    // 하단 버튼 클릭시
    private fun SelectButton() {
        try {
            Log.d(TAG, "SearchMap - SelectButton() called")
            val intent = Intent(this, MapApi::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.d(TAG, "SearchMap - SelectButton: ${e.message}")
        }
    }
    private fun BackActivity() {
        Log.d(TAG, "SearchMap - BackActivity() called")

        val intent = Intent(this, SelectActivity::class.java)
        startActivity(intent)
    }

}