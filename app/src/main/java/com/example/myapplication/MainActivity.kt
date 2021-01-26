package com.example.myapplication
//https://examples.javacodegeeks.com/android/core/os/handler/android-timer-example/
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.CalendarContract
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import okhttp3.*
import org.json.JSONArray
import java.io.*
import java.text.ParseException
import java.util.*


class MainActivity : Activity() {
    var game="[ミリシタ]"
    var ibemei="みりいべんと"
    var st = "2020-12-18T06:00:00Z"
    var en = "2020-12-24T12:00:00Z"
    //ぷろせｋ,ぽぷます、しゃに、でれ、みり日、みりK、みりC、さいどｍ、もばます
    var comboindex =0
    var googleapi="https://script.google.com/macros/s/AKfycbyQmmF6EGgRvfAfF8thzVnMNCRlJfh1dbYs_plQJ_9WwqzI4QR4lAjf/exec"

    private var startButton: Button? = null
    private var pauseButton: Button? = null
    private var Button2: Button? = null
    private var ICAL: Button? = null
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
        load()

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
             getgoogleapi()
             //getmobamasuapi()
             //getmatsuriapi("ko")// 日jaかなし  韓ko  香港zh
        }
        ICAL = findViewById<View>(R.id.ical) as Button
        ICAL!!.setOnClickListener {
            icalender()
        }
    }

    fun icalender(){

        try {
            var start=Date()
            var end=Date()

            val forma = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            start = forma.parse(st)
            end = forma.parse(en)

            //"vnd.android.cursor.dir/event"
            //https://developer.android.com/guide/topics/providers/calendar-provider?hl=ja&authuser=1
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, ibemei)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.time)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.time)
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }

        } catch (e: ParseException) {
            timerValue!!.text =elog(e)
        }

    }

    fun getgoogleapi(){
        val request = Request.Builder().url(googleapi).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        val sw = StringWriter()
                        val pw = PrintWriter(sw)
                        e.printStackTrace(pw)
                        pw.flush()
                        var str: String = sw.toString()
                        timerValue!!.text = str
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            try {
                            var jsonst = response.body!!.string()
                            val json = JSONArray(jsonst)
                            //["【復刻】Catch the shiny tail?","シャニマス","2021-01-22T15:00:00+09:00","2021-01-31T15:00:00+09:00"]
                            var cm = findViewById < View >(R.id.spinner) as Spinner
                            comboindex = cm.getSelectedItemPosition()

                            var a = json[comboindex].toString()//でふぉぷろせか
                            timerValue!!.text = a

                            val json2 = JSONArray(a)
                            st = json2[2].toString()
                            en = json2[3].toString()
                            game = "[" + json2[1].toString() + "]"
                            ibemei = game + json2[0].toString()
                            save()
                            }
                            catch (e: ParseException) {
                                timerValue!!.text =elog(e)
                            }

                        }
                    }
                }
        )



    }

    fun elog(e: Exception):String{
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        pw.flush()
            return   sw.toString()
    }

    fun load(){
        val pref = getSharedPreferences("timerini", MODE_PRIVATE)
        game =pref.getString("game", game).toString()
        st   = pref.getString("st", st).toString()
        en   = pref.getString("en", en).toString()
        ibemei = pref.getString("ibe", ibemei).toString()
        comboindex = pref.getInt("combo", comboindex)
        var cm = findViewById < View >(R.id.spinner) as Spinner
        cm.setSelection(comboindex)
    }

    fun save(){
        val pref = getSharedPreferences("timerini", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString("game", game)
        editor.putString("st", st)
        editor.putString("en", en)
        editor.putString("ibe", ibemei)
        editor.putInt("combo", comboindex)
        editor.commit()
    }


/*
//びるどしないので(),他サイトさまのapi使う場合
    fun gettoday():String{
        val today = Date()
        return SimpleDateFormat("yyyy-MM-dd").format(today)
    }

    fun getmobamasuapi(){
        var mobaapi="https://pink-check.school/api/v2/events/?time=" +gettoday() //"2022-02-28" //gettoday()
        val request = Request.Builder().url(mobaapi).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            var jsonst = response.body!!.string()
                            val json = JSONObject(jsonst)

                            //content[0]/detail/beginDateTime endDateTime
                            var content = json.getString("content")
                            var czrero = JSONArray(content)

                            game = "[モバマス]"

                            if(czrero.toString()=="[]"){
                                st=""
                                en=""
                                timerValue!!.text = json.toString()
                                return
                            }

                            val jsond = JSONObject(czrero[0].toString())

                            var detail = jsond.getString("detail")
                            var dzero = JSONArray(detail)
                            val jsontime = JSONObject(dzero[0].toString())

                            timerValue!!.text = jsontime.toString()

                            st = jsontime.getString("beginDateTime")
                            en = jsontime.getString("endDateTime")
                            ibemei = game + jsond.getString("name")
                        }
                    }
                }
        )



    }

    fun getmatsuriapi(country : String){
        var url = country+"/"
        var mobaapi="https://api.matsurihi.me/mltd/v1/"+url+"events/?at=" +gettoday() //"2021-01-17" //gettoday()
        val request = Request.Builder().url(mobaapi).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            var jsonst = response.body!!.string()
                            val json = JSONArray(jsonst)

                            game = "[ミリシタ]"
                            //[0]/schedule/beginDate endDate
                            if(json.toString()=="[]"){
                                st=""
                                en=""
                                timerValue!!.text = json.toString()
                                return
                            }

                            val jsond = JSONObject(json[0].toString())
                            var schedule = jsond.getString("schedule")
                            val jsontime = JSONObject(schedule.toString())

                            st = jsontime.getString("beginDate")
                            en = jsontime.getString("endDate")
                            timerValue!!.text = jsontime.toString()

                            ibemei = game + jsond.getString("name")
                        }
                    }
                }
        )



    }
*/

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

                var str: String = elog(e)
                timerValue!!.text = game +"エラー\r\n"+str
                if(st==""){
                    str= "開催中のイベントはありません\r\n" + str
                }
                else if(en==""){
                    str= "終了時間が不明です\r\n" + str
                }
                timerValue!!.text =str
                    return
            }
            var dateTimeTo =start.time
            var dateTimeFrom= end.time
            var nd= date.time
            var dd= (dateTimeFrom-dateTimeTo)/1000
            var ds= (-dateTimeTo+nd)/1000
            var de= (dateTimeFrom-nd)/1000
            var bar:Long=0
            if(dateTimeFrom==dateTimeTo){
                bar = 100
            }
            else {
                bar = ds * 100 / dd
            }
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