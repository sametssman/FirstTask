package com.sametsisman.firsttask

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {
    private lateinit var timer : CountDownTimer
    private var minute: Int? = null
    private var second : Int? = null
    private var prepareSecond : Int? = null
    private var time : Int? = null
    private var resumeTime : Int? = null
    private var remainMinute : Int? = null
    private var remainSecond : Int? = null
    private var remainPrepareMinute : Int? = null
    private var remainPrepareSecond : Int? = null
    private var resume : Boolean? = null
    private var preparePause : Boolean? = null
    private var mediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        init()

        imageStart.setOnClickListener{
            imageStart.isClickable = false
            imagePause.isClickable = true
            if(resume!!){
                infoText.text = "WORK"
                infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.bgcolor))
                textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.bgcolor))
                progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar)
                progress_countdown.background = resources.getDrawable(R.drawable.circle_shape)
                startTimer(resumeTime!!)
            }else if(preparePause!!){
                infoText.text = "PREPARE"
                infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
                textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
                progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar_paused)
                progress_countdown.background = resources.getDrawable(R.drawable.circle_shape_paused)
                startPrepare(resumeTime!!)
            }else{
                prepare()
            }
        }

        imagePause.setOnClickListener {
            if(preparePause!!){
                setCountdownText(0,remainPrepareSecond!!)
                resumeTime = remainPrepareSecond!!
            }else{
                setCountdownText(remainMinute!!,remainSecond!!)
                resumeTime = (remainMinute!!*60) + remainSecond!!
                resume = true
            }
            timer.cancel()
            imagePause.isClickable = false
            imageStart.isClickable = true
            progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar_paused)
            progress_countdown.background = resources.getDrawable(R.drawable.circle_shape_paused)
            infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
            textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
            infoText.text = "PAUSED"
        }

        imageRefresh.setOnClickListener {
            timer.cancel()
            imagePause.isClickable = false
            imageStart.isClickable = true
            setCountdownText(minute!!,second!!)
            progress_countdown.progress = 0
            progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar_paused)
            progress_countdown.background = resources.getDrawable(R.drawable.circle_shape_paused)
            infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
            textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
            infoText.text = "READY"
            resume = false
            preparePause = false
        }

        imageBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init(){
        imageStart.isClickable = true
        imagePause.isClickable = false
        imageRefresh.isClickable = false
        preparePause = false
        resume = false
        mediaPlayer = MediaPlayer.create(applicationContext,R.raw.voice)

        val sharedPreferences = getSharedPreferences("timeShared", MODE_PRIVATE)
        minute = sharedPreferences.getInt("minute",1)
        second = sharedPreferences.getInt("second",0)
        prepareSecond = sharedPreferences.getInt("prepareSecond",3)
        time = (minute!!*60) + second!!
        setCountdownText(minute!!,second!!)
        progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar_paused)
        progress_countdown.background = resources.getDrawable(R.drawable.circle_shape_paused)
        infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
        textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.paused_color))
        infoText.text = "READY"
    }

    private fun startPrepare(startTime: Int) {
        timer = object : CountDownTimer((startTime * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainTime = (p0/1000).toInt()
                remainPrepareMinute = remainTime / 60
                remainPrepareSecond = remainTime % 60
                setCountdownText(remainPrepareMinute!!,remainPrepareSecond!!)
                progress_countdown.progress = remainTime
            }

            override fun onFinish() {
                setCountdownText(minute!!,second!!)
                progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar)
                progress_countdown.background = resources.getDrawable(R.drawable.circle_shape)
                infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.bgcolor))
                textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.bgcolor))
                progress_countdown.progress = 0
                progress_countdown.max = time!!
                preparePause = false
                infoText.text = "WORK"
                startTimer(time!!)
            }

        }.start()
    }

    private fun startTimer(startTime: Int) {
        timer = object : CountDownTimer((startTime * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainTime = (p0/1000).toInt()
                remainMinute = remainTime / 60
                remainSecond = remainTime % 60
                setCountdownText(remainMinute!!,remainSecond!!)
                progress_countdown.progress = remainTime
            }

            override fun onFinish() {
                mediaPlayer!!.start()
                imagePause.isClickable = false
                imageStart.isClickable = false
                infoText.text = "END"
                infoText.setTextColor(ContextCompat.getColor(applicationContext,R.color.end_color))
                progress_countdown.progressDrawable = resources.getDrawable(R.drawable.circle_progress_bar_end)
                progress_countdown.background = resources.getDrawable(R.drawable.circle_shape_end)
                textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,R.color.end_color))
            }

        }.start()
    }

    private fun setCountdownText(minute: Int, second: Int){
        if(minute!!<10){
            if(second!!<10){
                textview_countdowntimer.text = "0$minute:0$second"
            }else{
                textview_countdowntimer.text = "0$minute:$second"
            }
        }else{
            if(second!!<10){
                textview_countdowntimer.text = "$minute:0$second"
            }else{
                textview_countdowntimer.text = "$minute:$second"
            }
        }
    }

    private fun prepare(){
        infoText.text = "PREPARE"
        preparePause = true
        progress_countdown.progress = 0
        progress_countdown.max = prepareSecond!!
        setCountdownText(0,prepareSecond!!)
        startPrepare(prepareSecond!!)
    }
}