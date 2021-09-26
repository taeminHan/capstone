package com.cap.withsullivan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        window.statusBarColor = Color.parseColor("#F1EFEF")

        UserApiClient.instance.me { user, error ->
            Glide.with(this).load("${user?.kakaoAccount?.profile?.profileImageUrl}").into(profileimg)
            nickname.text = "안녕하세요. ${user?.kakaoAccount?.profile?.nickname} 님"
        }

        mapbutton.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        detectmodebutton.setOnClickListener{
            val intent = Intent(this, DetectActivity::class.java)
            startActivity(intent)
        }

    }
}
