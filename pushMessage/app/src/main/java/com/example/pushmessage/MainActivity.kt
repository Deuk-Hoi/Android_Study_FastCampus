package com.example.pushmessage

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val resultTextView : TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val firebaseTokenTextView : TextView by lazy {
        findViewById(R.id.firebaseTokenTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updataResult()
        initFirebase()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        updataResult(true)
    }
    private fun initFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                val token = task.result
                firebaseTokenTextView.text = token
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updataResult(isNewIntent : Boolean = false){
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") + if(isNewIntent){
            "(으)로 갱신했습니다."
        }else{
            "(으)로 실행했습니다."
        }
    }
}

