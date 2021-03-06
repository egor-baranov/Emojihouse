package com.kepler88d.emojihouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var userData: User
    var idRoom = ""
    val list = mutableListOf<message>()
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

        if (savedInstanceState == null) {
            idRoom = intent.extras!!["id"].toString()
        }

        Log.d("checkextras", idRoom)
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

        binding.imageView.setOnClickListener {
            onBackPressed()
        }
        fetchRoomName()
        addListenerForMessages()
    }


    private fun fetchRoomName() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$idRoom")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("roomName").getValue().toString()
                binding.textView4.text = name
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addListenerForMessages() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$idRoom/messages")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    return
                }

                listOf(snapshot.children.last()).forEach {
                    val text = it.child("message").getValue().toString()
                    val sender = it.child("sender").getValue()
                    var senderName = ""
                    val refSender = FirebaseDatabase.getInstance(url).getReference("/users/$sender")
                    refSender.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            senderName = snapshot.child("username").getValue().toString()
                            val emoji = getRandomEmoji()
                            addMessage(senderName, emoji, text)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addMessage(username: String, icon: String, messageText: String) {
        if (messageText == null || messageText.trim() == "null") {
            return
        }

        val newView =
            LayoutInflater.from(this).inflate(R.layout.message_item, null, false)

        newView.findViewWithTag<TextView>("username").text = username
        newView.findViewWithTag<TextView>("icon").text = icon
        newView.findViewWithTag<TextView>("messageText").text = messageText

        binding.chatList.addView(newView)
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

    private fun getRandomEmoji(): String {
        return listOf(
            "😏", "🤣", "🤡", "😎",
            "🤥", "😉", "😳", "🧐",
            "🤓", "🤩", "🥳", "🤯",
            "🤪", "😋", "🤨", "😼",
            "🏘", "🏠", "🏚", "🏡"
        ).random()
    }

    private fun sendMessage() {
        val message = binding.textFieldUsername.editText!!.text.toString()
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$idRoom/messages")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                ref.child(snapshot.childrenCount.toString()).child("sender").setValue(userData.id)
                ref.child(snapshot.childrenCount.toString()).child("message").setValue(message)

            }
        })
        binding.textFieldUsername.editText!!.setText("")
    }

    data class message(
        val name: String,
        val icon: String,
        val text: String
    )
}