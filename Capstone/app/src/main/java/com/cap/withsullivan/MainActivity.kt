package com.cap.withsullivan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.cap.withsullivan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){

    private val TAG = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(applicationContext, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}