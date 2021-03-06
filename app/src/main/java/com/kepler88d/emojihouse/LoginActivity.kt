package com.kepler88d.emojihouse

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.kepler88d.emojihouse.databinding.ActivityLoginBinding
import java.util.*

const val url = "https://emojihouse-7b23f-default-rtdb.europe-west1.firebasedatabase.app/"

val requiredPermissions = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.INTERNET
)

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            300
        )

        FirebaseDatabase.getInstance(url).getReference("/users")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        attachTextChanger()
        addLoginButtonListener()
    }

    private fun addLoginButtonListener() {
        binding.loginButton.setOnClickListener {
            val text = binding.textFieldUsername.editText?.text
            if (text.isNullOrEmpty()) {
                binding.textFieldUsername.error = "Username can't be empty"
            } else if (!requiredPermissions.all {
                    ContextCompat.checkSelfPermission(
                        applicationContext, it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                Toast.makeText(
                    this,
                    "Not the all permissions were given by you",
                    Toast.LENGTH_SHORT
                ).show()

                ActivityCompat.requestPermissions(
                    this,
                    requiredPermissions,
                    300
                )
            } else {
                val userId = UUID.randomUUID().toString()
                val ref = FirebaseDatabase.getInstance(url).getReference("/users")
                val user = User(id = userId, username = text.toString(), profileImage = "")

                ref.child(userId).setValue(user)
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        saveUserData(user)
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Something's gone wrong. Try later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
        }
    }

    private fun saveUserData(user: User) {
        this.openFileOutput("id", Context.MODE_PRIVATE).write(user.id.toByteArray())
        this.openFileOutput("username", Context.MODE_PRIVATE).write(user.username.toByteArray())
        this.openFileOutput("profileImage", Context.MODE_PRIVATE)
            .write(user.profileImage.toByteArray())
    }

    private fun attachTextChanger() {
        binding.textFieldUsername.editText!!.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding.textFieldUsername.error = null
                binding.textView.text =
                    "\uD83D\uDC4B Hello${if (s.isNotBlank()) ", ${s.trim()}" else ""}."
            }
        })
    }
}

data class User(
    val id: String,
    val username: String,
    val profileImage: String = ""
)
