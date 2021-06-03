package com.example.secretdiary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    private val diaryTxt by lazy {
        findViewById<EditText>(R.id.diaryTxt)
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val getPrefence = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryTxt.setText(getPrefence.getString("diary", ""))

        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit{
                putString("diary",diaryTxt.text.toString())
            }
            Log.d("Diary", "Save!! ${diaryTxt.text.toString()}")
        }


        diaryTxt.addTextChangedListener{
            Log.d("Diary", "TextChange :: $it")
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 500)
        }
    }
}