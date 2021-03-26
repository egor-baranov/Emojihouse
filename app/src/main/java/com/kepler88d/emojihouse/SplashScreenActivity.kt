package com.kepler88d.emojihouse

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kepler88d.emojihouse.databinding.ActivitySplashScreenBinding
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        binding.emojihouse.text = "Emojihouse 🏠"
        findViewById<TextView>(R.id.splashScreenEmoji).text =
            listOf(
                "😏",
                "🤣",
                "🤡",
                "😎",
                "🤥",
                "😉",
                "😳",
                "🧐",
                "🤓",
                "🤩",
                "🥳",
                "🤯",
                "🤪",
                "😋",
                "🤨",
                "😼",
                "🏘",
                "🏠",
                "🏚",
                "🏡"
            ).random()

        val userDataFile = applicationContext.getFileStreamPath("id")
        val options = ActivityOptions.makeCustomAnimation(
            applicationContext,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        val intent =
            if (userDataFile != null && userDataFile.exists()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(intent, options.toBundle())
                // overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }, 1000)
    }
}