package com.kepler88d.emojihouse

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.kepler88d.emojihouse.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<TextInputLayout>(R.id.textField).editText!!.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                findViewById<TextView>(R.id.textView).text =
                    "\uD83D\uDC4B Hello${if (s.isNotBlank()) ", ${s.trim()}" else ""}."
                findViewById<TextView>(R.id.textView).text =
                    "\uD83D\uDC4B Hello${if (s.isNotBlank()) ", ${s.trim()}" else ""}."
            }
        })

        findViewById<MaterialButton>(R.id.loginButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}