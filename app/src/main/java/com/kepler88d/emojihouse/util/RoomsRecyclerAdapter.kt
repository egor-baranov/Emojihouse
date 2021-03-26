package com.kepler88d.emojihouse.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.ChatActivity
import com.kepler88d.emojihouse.R
import com.kepler88d.emojihouse.databinding.ChatItemBinding
import com.kepler88d.emojihouse.url

class RoomsRecyclerAdapter(private val context: Context, val list: List<Rooms>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class PostItem(val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val ref = FirebaseDatabase.getInstance(url)
                .getReference("/rooms/${list[position].id}/members")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.textView2.text = "${snapshot.children.count()} members"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            binding.textView3.text = list[position].roomName

            binding.constraintItem.setOnClickListener {
                showDialog(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatItemBinding.inflate(inflater, parent, false)
        return PostItem(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostItem).bind(position)
    }

    override fun getItemCount() = list.size

    private fun showDialog(position: Int) {

        val dialogInflater = LayoutInflater.from(context).inflate(R.layout.dialog_password, null)
        val passDialog =
            dialogInflater.findViewById(R.id.textInputLayout_EnterFormPassword) as TextInputLayout

        val builder = AlertDialog.Builder(context)
            .setView(dialogInflater)
            .setTitle("Input password")
            .setNegativeButton("Close") { _, _ -> }
            .setPositiveButton("Join") { _, _ ->
                val passwordRoom = passDialog.editText?.text.toString()
                if (passwordRoom == list[position].password) {
                    Toast.makeText(context, "+", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "-", Toast.LENGTH_SHORT).show()
                }
            }
        builder.create().show()
    }

}

data class Rooms(
    val id: String,
    val roomName: String,
    val password: String
)