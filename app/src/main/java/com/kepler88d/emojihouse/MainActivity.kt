package com.kepler88d.emojihouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kepler88d.emojihouse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userData = User("", "", "")

        openFileInput("id").use {
            userData.id = it.readBytes().toString(Charsets.UTF_8)
        }

        openFileInput("username").use {
            userData.username = it.readBytes().toString(Charsets.UTF_8)
        }

        openFileInput("profileImage").use {
            userData.profileImage = it.readBytes().toString(Charsets.UTF_8)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        for (i in 0..50) {
            addChat("chat $i", (5 * i + 327374) % 84)
        }

        val currentActivity = this
        binding.floatingActionButton.setOnClickListener {
            MaterialAlertDialogBuilder(currentActivity).setTitle("Choose an action 🤔")
                .setItems(arrayOf("😎 Create chat", "🥳 Join Chat")) { dialog, which ->
                    startActivity(
                        Intent(
                            currentActivity,
                            if (which == 0) AddNewRoomActivity::class.java else SearchRoomActivity::class.java
                        )
                    )
                }
                .show()
        }
    }

    private fun addChat(chatName: String, memberCount: Int) {
        val newView =
            LayoutInflater.from(this).inflate(R.layout.chat_item, null, false)
        newView.findViewWithTag<TextView>("channelName").text = chatName
        newView.findViewWithTag<TextView>("memberCount").text = "$memberCount members"

        newView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.chatList).addView(newView)
    }
}