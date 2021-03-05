package com.kepler88d.emojihouse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.kepler88d.emojihouse.databinding.ActivityLoginBinding
import java.util.*


class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseDatabase.getInstance().getReference("/users/$filename")
                val user = User(text.toString(), "")
                ref.setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
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