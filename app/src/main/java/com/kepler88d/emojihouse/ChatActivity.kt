package com.kepler88d.emojihouse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var userData: User

    lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    var firstlaunch = true
    var idRoom = ""
    val emojiList = listOf(
        "😏", "🤣", "🤡", "😎",
        "🤥", "😉", "😳", "🧐",
        "🤓", "🤩", "🥳", "🤯",
        "🤪", "😋", "🤨", "😼",
        "🏘", "🏠", "🏚", "🏡",
        "🥶", "🥴", "👺", "🤖",
        "💩", "🐸", "🐖"
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

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet))

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.textFieldUsername.editText!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.textFieldUsername.editText!!.isFocusedByDefault = true

        binding.menuButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.chat_dropdown_menu)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }


    private fun fetchRoomName() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$idRoom")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("roomName").getValue().toString()
                binding.textView4.text = name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addListenerForMessages() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$idRoom/messages")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    return
                }
                if (!firstlaunch) {
                    listOf(snapshot.children.last()).forEach {
                        val text = it.child("message").getValue().toString()
                        val sender = it.child("sender").getValue()
                        var senderName = ""
                        val refSender =
                            FirebaseDatabase.getInstance(url).getReference("/users/$sender")
                        refSender.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                senderName = snapshot.child("username").getValue().toString()
                                val emoji = snapshot.child("profileImage").getValue().toString()
                                addMessage(senderName, emoji, text)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                } else {
                    snapshot.children.forEach {
                        firstlaunch = false
                        val text = it.child("message").getValue().toString()
                        val sender = it.child("sender").getValue()
                        var senderName = ""
                        val refSender =
                            FirebaseDatabase.getInstance(url).getReference("/users/$sender")
                        refSender.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                senderName = snapshot.child("username").getValue().toString()
                                val emoji = snapshot.child("profileImage").getValue().toString()
                                addMessage(senderName, emoji, text)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        val contextMenuTextView = v as TextView
        val context = this
        // Add menu items via menu.add
        menu.add("a")
            .setOnMenuItemClickListener { item: MenuItem? ->
                true
            }
        menu.add("b")
            .setOnMenuItemClickListener { item: MenuItem? ->
                true
            }
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
        binding.nestedScrollView.post {
            binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun loadKeyboard() {
        for (i in 0 until (emojiList.size / 5)) {
            val newView =
                LayoutInflater.from(this).inflate(R.layout.emoji_line, null, false)

            for (j in 0 until 5) {
                newView.findViewWithTag<MaterialButton>("button$j").text = emojiList[i * 5 + j]
                newView.findViewWithTag<MaterialButton>("button$j").setOnClickListener {
                    binding.textFieldUsername.editText!!.append(emojiList[i * 5 + j])
                }
            }

            binding.emojiLayout.addView(newView)
        }
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