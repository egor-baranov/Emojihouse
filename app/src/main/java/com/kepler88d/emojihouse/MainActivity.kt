package com.kepler88d.emojihouse

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.databinding.ActivityMainBinding
import com.kepler88d.emojihouse.fragments.ChatFragment

class MainActivity : FragmentActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var userData: User

    lateinit var roomId: String

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
        fetchRooms()

        binding.floatingActionButton.setOnClickListener {
            showMenuDialog()
        }
    }

    private fun showMenuDialog() {
        MaterialAlertDialogBuilder(this).setTitle("Choose an action 🤔")
            .setItems(arrayOf("😎 Create chat", "🥳 Join Chat")) { _, which ->
                if (which == 0) {
                    startActivity(Intent(this, AddNewRoomActivity::class.java))
                } else {
                    val builder = AlertDialog.Builder(this)
                        .setView(
                            LayoutInflater.from(this)
                                .inflate(R.layout.dialog_password, null)
                        )
                        .setTitle("Enter invite code")
                        .setNegativeButton("Close") { _, _ -> }
                        .setPositiveButton("Join") { _, _ ->
                            joinRoom()
                        }
                    builder.create().show()
                }
            }.show()
    }

    private fun joinRoom() {
        val passwordRoom = (
                LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
                    .findViewById(R.id.textInputLayout_EnterFormPassword) as TextInputLayout
                )
            .editText?.text.toString()

        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if (it.child("password").value == passwordRoom) {
                        val refSubscribeUser = FirebaseDatabase.getInstance(url)
                            .getReference("/users/${userData.id}/subscriptions")
                        refSubscribeUser.child(it.key.toString()).setValue("")
                        ref.child(it.key.toString()).child("members").child(userData.id)
                            .setValue("")

                        addChat(
                            it.child("roomName").value.toString(),
                            it.child("members").childrenCount.toInt(),
                            it.key.toString(),
                            it.child("picture").value.toString()
                        )
                        return@forEach
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchRooms() {
        val rooms = mutableListOf<String>()
        val ref = FirebaseDatabase.getInstance(url).getReference(
            "/users/${userData.id}/subscriptions"
        )
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    rooms.add(it.key!!)
                }

                rooms.forEach {
                    FirebaseDatabase.getInstance(url).getReference("/rooms")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val child = snapshot.child(it)
                                addChat(
                                    child.child("roomName").value.toString(),
                                    child.child("members").children.count(),
                                    child.key.toString(),
                                    child.child("password").value.toString()
                                )
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        ref.onDisconnect()
    }

    private fun addChat(chatName: String, memberCount: Int, id: String, pic: String) {
        val newView =
            LayoutInflater.from(this).inflate(R.layout.chat_item, null, false)
        newView.findViewWithTag<TextView>("channelName").text = chatName
        newView.findViewWithTag<TextView>("memberCount").text = "$memberCount members"
        newView.findViewWithTag<TextView>("img").text = pic.take(2)

        newView.setOnClickListener {
            roomId = id
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ChatFragment())
                .commit()
        }

        findViewById<LinearLayout>(R.id.chatList).addView(newView)
    }
}