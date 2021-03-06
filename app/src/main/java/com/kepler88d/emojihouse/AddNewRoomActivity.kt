package com.kepler88d.emojihouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.kepler88d.emojihouse.databinding.ActivityAddNewRoomBinding
import java.util.*

//val url = "https://emojihouse-7b23f-default-rtdb.europe-west1.firebasedatabase.app/"
class AddNewRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddNewRoomBinding
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_room)

        addCreateButtonListener()
    }

    private fun addCreateButtonListener() {
        binding.buttonCreateRoom.setOnClickListener {
            val textRoom = binding.textFieldRoomname.editText?.text.toString()

            val password = generatePassword()
            val pic = password[0]
            if (textRoom.isEmpty()) {
                binding.textFieldRoomname.error = "Room name can't be empty"
            }

            if (textRoom.isNotEmpty() && password.isNotEmpty()) {
                val roomId = UUID.randomUUID().toString()
                val mChannel = NewChannel(textRoom, password)
                val ref = FirebaseDatabase.getInstance(url).getReference("/rooms")
//                ref.child(roomId).child("picture").setValue(pic)
                ref.child(roomId).setValue(mChannel)
                    .addOnSuccessListener {
                        addRoomToUser(roomId)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Something's gone wrong", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun generatePassword(): String {
        val emojiList = listOf(
            "😏", "🤣", "🤡", "😎",
            "🤥", "😉", "😳", "🧐",
            "🤓", "🤩", "🥳", "🤯",
            "🤪", "😋", "🤨", "😼",
            "🏘", "🏠", "🏚", "🏡"
        )

        var ret = ""
        for (i in 0..5) {
            ret += emojiList.random()
        }
        return ret
    }

    private fun addRoomToUser(roomId: String) {
        val ref = FirebaseDatabase.getInstance(url).getReference("/users/${userData.id}")
        ref.child("subscriptions").child(roomId).setValue("")
            .addOnSuccessListener {
                addUserToRoom(roomId)
            }
    }

    private fun addUserToRoom(roomId: String) {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$roomId/members")
        ref.child(userData.id).setValue("")
    }

    data class NewChannel(
        val roomName: String,
        val password: String
    )
}