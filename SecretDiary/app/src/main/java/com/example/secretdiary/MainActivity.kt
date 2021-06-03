package com.example.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private val numberPicker1 by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2 by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3 by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton by lazy {
        findViewById<Button>(R.id.openButton)
    }

    private val changeButton by lazy {
        findViewById<Button>(R.id.changeButton)
    }

    private var changePassMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if (changePassMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val getpreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val password = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if (getpreference.getString("password", "000") == password) {
                startActivity(Intent(this, DiaryActivity::class.java))
            } else {
                showErrorDialog()
            }

        }
        changeButton.setOnClickListener {
            val getpreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val password = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            if (changePassMode) {
                changePassMode = false
                getpreference.edit(true) {
                    putString("password", password)
                }
                changeButton.setBackgroundColor(Color.BLACK)

            } else {
                if (getpreference.getString("password", "000") == password) {
                    changePassMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해 주세요", Toast.LENGTH_SHORT).show()
                    changeButton.setBackgroundColor(Color.RED)
                } else {
                    showErrorDialog()
                }
            }
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("비밀번호 오류")
            .setMessage("비밀번호가 일치하지 않습니다.")
            .setPositiveButton("확인", null)
            .show()
    }
}