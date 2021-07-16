package com.example.alarmapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //step0 뷰를 초기화 해주기

        initOnOffButton()
        initChangeAlarmTimeButton()

        //step1 데이터 가져오기
        val model = fetchDataFromSharedPreferences()
        renderView(model)

        //step2 뷰에 데이터를 그려주기


    }

    private fun initOnOffButton(){
        val onOffButton = findViewById<Button>(R.id.onoffButton)
        onOffButton.setOnClickListener {

            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener

            val newModel = saveAlarmModel(model.hour, model.minute, model.onoff.not())

            renderView(newModel)

            if(newModel.onoff){
                // 켜짐 -> 알람을 등록
                val calandar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)
                    if(before(Calendar.getInstance())){
                        add(Calendar.DATE, 1)
                    }
                }
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARAM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calandar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            }else{
                cancelAlarm()
            }
        }
    }

    private fun initChangeAlarmTimeButton(){
        val changeAlarmButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        //현재시간을 일단 가져온다.
        changeAlarmButton.setOnClickListener {
            val calender = Calendar.getInstance()
            //TimePickDialog를 띄우고 시간을 설정하도록 하게끔하고, 시간을 가져온다.
            TimePickerDialog(this, { picker, hour, minute ->
                val model = saveAlarmModel(hour, minute, false)
                renderView(model)
                cancelAlarm()
                //데이터를 저장
                //뷰를 업데이트한다.
                //기존에 있던 알람을 삭제
            }, calender.get(Calendar.HOUR), calender.get(Calendar.MINUTE), false).show()
        }
    }

    private fun saveAlarmModel(hour: Int, minute: Int, onOff : Boolean): AlarmDisplayModel{
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onoff = onOff
        )

        val sharedPreference = getSharedPreferences("time", Context.MODE_PRIVATE)
        with(sharedPreference.edit()){
            putString("alarm", model.makeDataForDB())
            putBoolean("onOff", model.onoff)
            commit()
        }

        return model
    }

    private fun fetchDataFromSharedPreferences():AlarmDisplayModel{
        val sharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        val timeDBValue = sharedPreferences.getString("alarm", "9:30") ?: "9:30"

        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")
        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onoff = onOffDBValue
        )

        val pendingIntent = PendingIntent.getBroadcast(this, ALARAM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)
        if((pendingIntent == null) and alarmModel.onoff){
            //알람은 꺼져있는데 데이터는 켜져있는경우
            alarmModel.onoff = false
        }else if((pendingIntent != null) and alarmModel.onoff.not()){
            //알람은 켜져 있는데, 데이터는 꺼져있는경우
                // 알람을 취소
            pendingIntent.cancel()
        }
        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel){
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.onoffButton).apply {
            text = model.onoffText
            tag = model
        }
    }

    private fun cancelAlarm(){
        val pendingIntent = PendingIntent.getBroadcast(this, ALARAM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }

    companion object{
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARAM_REQUEST_CODE = 1000
    }
}