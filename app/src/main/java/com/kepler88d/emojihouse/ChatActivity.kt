package com.kepler88d.emojihouse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.kepler88d.emojihouse.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var userData: User

    val emojiList = listOf(
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
    )

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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.textFieldUsername.editText!!.showSoftInputOnFocus = false

        loadKeyboard()
        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        binding.imageView.setOnClickListener{
            onBackPressed()
        }


    }

    private fun loadKeyboard() {
        for (i in 0 until (emojiList.size / 3)) {
            val newView =
                LayoutInflater.from(this).inflate(R.layout.emoji_line, null, false)

            for (j in 0 until 3) {
                newView.findViewWithTag<MaterialButton>("button$j").text = emojiList[i * 3 + j]
                newView.findViewWithTag<MaterialButton>("button$j").setOnClickListener {
                    binding.textFieldUsername.editText!!.append(emojiList[i * 3 + j])
                }
            }

            binding.emojiLayout.addView(newView)
        }
    }

    private fun sendMessage() {
        binding.textFieldUsername.editText!!.setText("")
    }
}