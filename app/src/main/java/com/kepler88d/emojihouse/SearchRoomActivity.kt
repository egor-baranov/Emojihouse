package com.kepler88d.emojihouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.databinding.ActivitySearchRoomBinding

class SearchRoomActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_room)
        val list = mutableListOf<Rooms>()
        val adapter = RoomsRecyclerAdapter(this, list)
        binding.recyclerViewRooms.adapter = adapter

        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val id = it.key.toString()
                    val password = it.child("password").getValue().toString()
                    val name = it.child("roomName").getValue().toString()

                    list.add(Rooms(id, name, password))
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}