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
    lateinit var binding : ActivityAddNewRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_room)

        addCreateButtonListener()
    }

    private fun addCreateButtonListener() {
        binding.buttonCreateRoom.setOnClickListener {
            val textRoom = binding.textFieldRoomname.editText?.text.toString()
            val password = binding.textFieldPassword.editText?.text.toString()
            if(textRoom.isEmpty()){
                binding.textFieldRoomname.error = "Room name can't be empty"
            }
            if(password.isEmpty()){
                binding.textFieldPassword.error = "Password can't be empty"
            }
            if(textRoom.isNotEmpty() && password.isNotEmpty()){
                val roomId = UUID.randomUUID().toString()
                val mChannel = NewChannel(textRoom, password)
                val ref = FirebaseDatabase.getInstance(url).getReference("/rooms")
                ref.child(roomId).setValue(mChannel)
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Something's gone wrong", Toast.LENGTH_SHORT).show()
                    }
            }


        }
    }

    data class NewChannel(
        val roomName : String,
        val password : String
    )
}