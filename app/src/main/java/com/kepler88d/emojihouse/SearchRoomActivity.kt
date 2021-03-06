package com.kepler88d.emojihouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.kepler88d.emojihouse.databinding.ActivitySearchRoomBinding

class SearchRoomActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_room)

    }
}