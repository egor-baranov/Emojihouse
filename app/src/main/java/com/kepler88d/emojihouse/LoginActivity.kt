package com.kepler88d.emojihouse

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kepler88d.emojihouse.databinding.ActivityLoginBinding
import java.util.*

val url = "https://emojihouse-7b23f-default-rtdb.europe-west1.firebasedatabase.app/"
class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ref = FirebaseDatabase.getInstance(url).getReference("/users")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        attachTextChanger()
        addLoginButtonListener()
    }

    private fun addLoginButtonListener() {
        binding.loginButton.setOnClickListener {
            val text = binding.textFieldUsername.editText?.text
            if(text.isNullOrEmpty()){
                binding.textFieldUsername.error = "Input your nickname"
            }
            else{
                val userId = UUID.randomUUID().toString()
                val ref = FirebaseDatabase.getInstance(url).getReference("/users")
                val user = User(text.toString(), "")

                ref.child(userId).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Something's gone wrong. Try later", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }
    data class User(
        val nickname : String,
        val profile_img : String = ""
    )
    private fun attachTextChanger(){
        binding.textFieldUsername.editText!!.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.textFieldUsername.error = null
                binding.textView.text = "\uD83D\uDC4B Hello${if(s.isNotBlank()) ", ${s.trim()}" else ""}."
            }
        })
    }
}