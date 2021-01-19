package com.example.myapplication
//https://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
import android.app.Activity
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.text.ParseException
import java.util.*

class MainActivity : Activity() {
    var ibemei="みりいべんと"
    var st = "2020-12-18T06:00:00Z"
    var en = "2020-12-24T12:00:00Z"
    var googleapi="https://script.google.com/macros/s/AKfycbyQmmF6EGgRvfAfF8thzVnMNCRlJfh1dbYs_plQJ_9WwqzI4QR4lAjf/exec"

    private var startButton: Button? = null
    private var pauseButton: Button? = null
    private var Button2: Button? = null
    private var timerValue: TextView? = null
    private var progressBar: ProgressBar? = null
    private var startTime = 0L
    private val customHandler = Handler()
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timerValue = findViewById<View>(R.id.timerValue) as TextView

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar

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
        Button2 = findViewById<View>(R.id.button2) as Button
        Button2!!.setOnClickListener {

            val request = Request.Builder().url(googleapi).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        var jsonst = response.body!!.string()
                        val json = JSONArray(jsonst)
                        //0しゃに、1でれ、2みり日、みりK、みりC、5さいどｍ、6もばます、7ぷろせｋ
                        //["【復刻】Catch the shiny tail?","シャニマス","2021-01-22T15:00:00+09:00","2021-01-31T15:00:00+09:00"]
                        var a = json[7].toString()//でふぉぷろせか
                        timerValue!!.text = a

                        val json2 = JSONArray(a)
                        st = json2[2].toString()
                        en = json2[3].toString()
                       var game = "["+json2[1].toString() +"]"
                        ibemei = game + json2[0].toString()
                    }
                }
            })
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds

            val date = Date()
            val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ")
            var nn=format.format(date)

            var start=Date()
            var end=Date()


            try {
                val forma = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                start = forma.parse(st)
                end = forma.parse(en)

            } catch (e: ParseException) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                pw.flush()
                var str: String = sw.toString()
                if(en==""){
                    str= "終了時間が不明です\r\n" + str
                }
                timerValue!!.text = str
                    return
            }
            var dateTimeTo =start.time
            var dateTimeFrom= end.time
            var nd= date.time
            var dd= (dateTimeFrom-dateTimeTo)/1000
            var ds= (-dateTimeTo+nd)/1000
            var de= (dateTimeFrom-nd)/1000
            var bar = ds*100/dd
            if(ds<0){
                ds=0
                bar=0
            }
            if(de<0){
                ds=dd
                bar=100
                de=0
            }

            var dds=  dtime(dd)
            var dss=  dtime(ds)
            var des= dtime(de)

            var sts=format.format(start)
            var ste=format.format(end)


            timerValue!!.text = ibemei+"\r\n現在:"+nn + "\r\n開始:" + sts +"\r\n終了:"+
                    ste +"\r\n期間:" + dds +"\r\n経過:" + dss +"\r\n残り:" + des +"\r\n進捗:" + bar +"%"

            val l= bar.toInt()
            progressBar!!.setProgress(l);
            customHandler.postDelayed(this, 0)
        }

        fun dtime(dt: Long):String{
            if(dt<0) {return "0日0時間0分"}
            var minutes  = ((dt / 60) % 60)
            var hours    = ((dt / 3600) % 24)
            var days     = (dt / 86400)
            var timest = days.toString() +"日" +hours.toString()+"時間"+minutes.toString() +"分"
            return timest
        }

    }



}