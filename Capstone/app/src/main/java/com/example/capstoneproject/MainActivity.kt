package com.example.capstoneproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.capstoneproject.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 활성화
    private lateinit var binding: ActivityMainBinding

    // 구글 로그인 클라이언트 활성화
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    // 상수
    private companion object {
        private const val RC_SIGN_IN = 100
        const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 바인딩 활성화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 구글 로그인 구성하기
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.defaultwebclientid))
                .requestEmail() // 구글 이메일 로그인
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // firebase 인증 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // 구글 로그인 버튼 클릭 리스너 설정
        binding.googleSignInBtn.setOnClickListener {
            Log.d(TAG, "onCreate: begin Google Sign In")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        // 카카오 로그인 버튼 클릭 리스너 설정
        binding.KakaoSignInBtn.setOnClickListener {
            onLoginButtonClicked()
        }
    }

    private fun checkUser() {
        // 로그인 체크
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // 이미 로그인 상태
            // 활동 시작
            startActivity(Intent(this@MainActivity, SelectActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 구굴 로그인api 사용 후 결과
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google Sign In result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 로그인 성공
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e: Exception) {
                // 로그인 실패시
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Logged In")
                    // get loggedIn user
                    val firebaseUser = firebaseAuth.currentUser
                    // get user info
                    val uid = firebaseUser!!.uid
                    val email = firebaseUser.email

                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $uid")
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                    // 클릭시 발생 이벤트
                    if (authResult.additionalUserInfo!!.isNewUser) {
                        // 새로운 유저 생성
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created... \n$email")
                        Toast.makeText(this@MainActivity, "Account created... \n$email", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        // 기존 유저 로그인
                        Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user... \n$email")
                        Toast.makeText(this@MainActivity, "Logged In... \n$email", Toast.LENGTH_SHORT).show()
                    }

                    // 활동 시작
                    startActivity(Intent(this@MainActivity, SelectActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Log In Failed due to ${e.message}")
                    Toast.makeText(this@MainActivity, "Log In Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }

    // 다음 화면으로 전환
    private fun onLoginButtonClicked() {
        try {
            Log.d(TAG, "MainActivity - onLoginButtonClicked() called")
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
        } catch (e : Exception) {
            Log.d(TAG, "실패 ${e.message}")
        }

    }

}