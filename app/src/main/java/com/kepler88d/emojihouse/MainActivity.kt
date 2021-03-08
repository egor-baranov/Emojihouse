package com.kepler88d.emojihouse

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


        fetchRooms()



        val currentActivity = this
        binding.floatingActionButton.setOnClickListener {
            MaterialAlertDialogBuilder(currentActivity).setTitle("Choose an action 🤔")
                .setItems(arrayOf("😎 Create chat", "🥳 Join Chat")) { dialog, which ->
                    if(which == 0){
                        startActivity(Intent(this, AddNewRoomActivity::class.java))
                    }
                    else if(which == 1){
                        val dialogInflater = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
                        val passDialog = dialogInflater.findViewById(R.id.textInputLayout_EnterFormPassword) as TextInputLayout

                        val builder = AlertDialog.Builder(this)
                            .setView(dialogInflater)
                            .setTitle("Enter invite code")
                            .setNegativeButton("Close"){dialog, which ->  }
                            .setPositiveButton("Join"){dialog, which ->
                                val passwordRoom = passDialog.editText?.text.toString()
                                val ref = FirebaseDatabase.getInstance(url).getReference("/rooms")
                                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {

                                        snapshot.children.forEach {
                                            if(it.child("password").getValue() == passwordRoom){
                                                val refSubscribeUser = FirebaseDatabase.getInstance(url).getReference("/users/${userData.id}/subscriptions")
                                                refSubscribeUser.child(it.key.toString()).setValue("")
                                                ref.child(it.key.toString()).child("members").child(userData.id).setValue("")
                                                val name = it.child("roomName").getValue().toString()
                                                val id = it.key.toString()
                                                val count = it.child("members").childrenCount.toInt()
                                                val pic = it.child("picture").getValue().toString()
                                                addChat(name, count, id, pic)
                                                return@forEach
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                            }
                        builder.create().show()
                    }


                }
                .show()
        }
    }

    private fun fetchRooms(){
        val rooms = mutableListOf<String>()
        val ref = FirebaseDatabase.getInstance(url).getReference("/users/${userData.id}/subscriptions")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val id = it.key
                    Log.d("checkfetch", "$id")
                    rooms.add(id!!)
                }
                val refRooms = FirebaseDatabase.getInstance(url).getReference("/rooms")
                rooms.forEach {
                    Log.d("checkfetch", " 1 $it")
                    val room = refRooms
                    room.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val name = snapshot.child(it).child("roomName").getValue().toString()
                            var count = 0
                            val pic = snapshot.child(it).child("password").getValue().toString()
                            val id = snapshot.child(it).key.toString()
//                            if(name.isNullOrEmpty()){
                                snapshot.child(it).child("members").children.forEach {
                                    count++
                                }
                                Log.d("checkfetch", "$name $count $id")
                                addChat(name, count, id, pic)
//                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        ref.onDisconnect()


    }

    private fun addChat(chatName: String, memberCount: Int, id: String, pic : String) {
        val newView =
            LayoutInflater.from(this).inflate(R.layout.chat_item, null, false)
        newView.findViewWithTag<TextView>("channelName").text = chatName
        newView.findViewWithTag<TextView>("memberCount").text = "$memberCount members"
        newView.findViewWithTag<TextView>("img").text = pic.take(2)
        newView.setOnClickListener {
            Log.d("checkfetch", id)
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.chatList).addView(newView)
    }

    data class Rooms(
        val id: String,
        val roomName: String,
        val password: String
    )
}