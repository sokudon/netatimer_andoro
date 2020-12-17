package com.example.myapplication
//https://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.text.ParseException
import java.util.*


class MainActivity : Activity() {
    private var startButton: Button? = null
    private var pauseButton: Button? = null
    private var timerValue: TextView? = null
    private var startTime = 0L
    private val customHandler = Handler()
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timerValue = findViewById<View>(R.id.timerValue) as TextView
        startButton = findViewById<View>(R.id.startButton) as Button
        startButton!!.setOnClickListener {
            startTime = SystemClock.uptimeMillis()
            customHandler.postDelayed(updateTimerThread, 0)
        }
        pauseButton = findViewById<View>(R.id.pauseButton) as Button
        pauseButton!!.setOnClickListener {
            timeSwapBuff += timeInMilliseconds
            customHandler.removeCallbacks(updateTimerThread)
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds

            val date = Date()
            val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ")
            var nn=format.format(date)
            val st = "2020/12/10 15:00"
            val en = "2020/12/18 21:00"
            var start=Date()
            var end=Date()


            try {
                val forma = SimpleDateFormat("yyyy/MM/dd hh:mm")
                start = forma.parse(st)
                end = forma.parse(en)

            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var dateTimeTo =start.getTime()
            var dateTimeFrom= end.getTime()
            var nd= date.getTime()
            var dd= (dateTimeFrom-dateTimeTo)/1000
            var ds= (-dateTimeTo+nd)/1000
            var de= (dateTimeFrom-nd)/1000

            var dds= (dd / 86400).toString() +"日" + ((dd / 3600) % 24).toString() +"時間"+
                    ((dd / 60) % 60).toString()+"分" //+((dd / 10) % 60) +"秒"
            var dss= (ds / 86400).toString() +"日" + ((ds / 3600) % 24).toString() +"時間"+
                    ((ds / 60) % 60).toString()+"分"//+((ds / 10) % 60) +"秒"
            var des= (de / 86400).toString() +"日" + ((de / 3600) % 24).toString() +"時間"+
                    ((de / 60) % 60).toString()+"分"//+((de / 10) % 60) +"秒"

            timerValue!!.text = nn + "\r\n" + start +"\r\n"+
             end +"\r\n" + dds +"\r\n" + dss +"\r\n" + des
            customHandler.postDelayed(this, 0)
        }

        //fun dtime(dt){
        //        if(dt<0) {return "0日0時間0分"}
        // dt=Math.abs(dt)
        //var minutes  = Math.floor((dt / 60) % 60)
        //var hours    = Math.floor((dt / 3600) % 24)
        //var days     = Math.floor(dt / 86400)
        //var tmp = days +"日" +hours+"時間"+minutes +"分"
        //return tmp
        //}

    }



}