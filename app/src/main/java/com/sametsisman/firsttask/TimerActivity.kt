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
    private lateinit var timer1 : CountDownTimer
    private lateinit var timer2 : CountDownTimer
    private var timer1IsRunning : Boolean? = null
    private var timer2IsRunning : Boolean? = null
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
                setTimerUi("WORK",R.color.bgcolor,R.drawable.circle_progress_bar,R.drawable.circle_shape)
                startTimer(resumeTime!!)
            }else if(preparePause!!){
                setTimerUi("PREPARE",R.color.paused_color,R.drawable.circle_progress_bar_paused,R.drawable.circle_shape_paused)
                startPrepare(resumeTime!!)
            }else{
                prepare()
            }
        }

        imagePause.setOnClickListener {
            if(preparePause!!){
                timer1.cancel()
                setCountdownText(0,remainPrepareSecond!!)
                resumeTime = remainPrepareSecond!!
            }else{
                timer2.cancel()
                setCountdownText(remainMinute!!,remainSecond!!)
                resumeTime = (remainMinute!!*60) + remainSecond!!
                resume = true
            }
            imagePause.isClickable = false
            imageStart.isClickable = true
            setTimerUi("PAUSED",R.color.paused_color,R.drawable.circle_progress_bar_paused,R.drawable.circle_shape_paused)
        }

        imageRefresh.setOnClickListener {
            if (timer1IsRunning!!){
                timer1.cancel()
            }else if (timer2IsRunning!!){
                timer2.cancel()
            }
            imagePause.isClickable = false
            imageStart.isClickable = true
            setCountdownText(minute!!,second!!)
            progress_countdown.progress = 0
            setTimerUi("READY",R.color.paused_color,R.drawable.circle_progress_bar_paused,R.drawable.circle_shape_paused)
            resume = false
            preparePause = false
        }

        imageBack.setOnClickListener {
            if (timer1IsRunning!!){
                timer1.cancel()
            }else if (timer2IsRunning!!){
                timer2.cancel()
            }
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init(){
        imageStart.isClickable = true
        imagePause.isClickable = false
        imageRefresh.isClickable = false
        timer1IsRunning = false
        timer2IsRunning = false
        preparePause = false
        resume = false
        mediaPlayer = MediaPlayer.create(applicationContext,R.raw.voice)

        val sharedPreferences = getSharedPreferences("timeShared", MODE_PRIVATE)
        minute = sharedPreferences.getInt("minute",1)
        second = sharedPreferences.getInt("second",0)
        minute = minute!! + (second!!/60)
        second = second!! % 60
        prepareSecond = sharedPreferences.getInt("prepareSecond",3)
        time = (minute!!*60) + second!!
        setCountdownText(minute!!,second!!)
        setTimerUi("READY",R.color.paused_color,R.drawable.circle_progress_bar_paused,R.drawable.circle_shape_paused)
    }

    private fun startPrepare(startTime: Int) {
        timer1 = object : CountDownTimer((startTime * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainTime = (p0/1000).toInt()
                remainPrepareMinute = remainTime / 60
                remainPrepareSecond = remainTime % 60
                setCountdownText(remainPrepareMinute!!,remainPrepareSecond!!)
                progress_countdown.progress = remainTime
                timer1IsRunning = true
            }

            override fun onFinish() {
                setCountdownText(minute!!,second!!)
                setTimerUi("WORK",R.color.bgcolor,R.drawable.circle_progress_bar,R.drawable.circle_shape)
                progress_countdown.progress = 0
                progress_countdown.max = time!!
                preparePause = false
                startTimer(time!!)
                timer1IsRunning = false
            }

        }.start()
    }

    private fun startTimer(startTime: Int) {
        timer2 = object : CountDownTimer((startTime * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainTime = (p0/1000).toInt()
                remainMinute = remainTime / 60
                remainSecond = remainTime % 60
                setCountdownText(remainMinute!!,remainSecond!!)
                progress_countdown.progress = remainTime
                timer2IsRunning = true
            }

            override fun onFinish() {
                mediaPlayer!!.start()
                imagePause.isClickable = false
                imageStart.isClickable = false
                setTimerUi("END",R.color.end_color,R.drawable.circle_progress_bar_end,R.drawable.circle_shape_end)
                timer2IsRunning = false
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

    private fun setTimerUi(textInfo : String,textColor : Int,progressColor : Int,progressBackgorund : Int){
        infoText.text = textInfo
        infoText.setTextColor(ContextCompat.getColor(applicationContext,textColor))
        textview_countdowntimer.setTextColor(ContextCompat.getColor(applicationContext,textColor))
        progress_countdown.progressDrawable = resources.getDrawable(progressColor)
        progress_countdown.background = resources.getDrawable(progressBackgorund)
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