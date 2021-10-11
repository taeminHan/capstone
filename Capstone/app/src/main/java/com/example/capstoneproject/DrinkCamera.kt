package com.example.capstoneproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstoneproject.databinding.ActivitySelectBinding

class DrinkCamera : AppCompatActivity() {

    private lateinit var binding: ActivitySelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_drink_camera)

        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}