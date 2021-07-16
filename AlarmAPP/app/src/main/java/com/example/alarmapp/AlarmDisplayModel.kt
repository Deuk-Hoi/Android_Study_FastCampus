package com.example.alarmapp

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onoff: Boolean
){
    val timeText : String
        get(){
            val h = "%02d".format(if(hour < 12) hour else (hour -12))
            val m = "%02d".format(minute)
            return "$h:$m"
        }
    val ampmText : String
        get(){
            return if(hour < 12) "AM" else "PM"
        }
    val onoffText : String
        get(){
            return if(onoff) "알람 끄기" else "알람 켜기"
        }
    fun makeDataForDB(): String{
        return "$hour: $minute"
    }
}
