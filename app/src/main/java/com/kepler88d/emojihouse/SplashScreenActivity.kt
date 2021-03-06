package com.kepler88d.emojihouse

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

        val currentActivity = this
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val userDataFile = applicationContext.getFileStreamPath("id")

                val intent =
                    if (userDataFile != null && userDataFile.exists()) {
                        Intent(currentActivity, MainActivity::class.java)
                    } else {
                        Intent(currentActivity, LoginActivity::class.java)
                    }
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }, 3000)
    }
}