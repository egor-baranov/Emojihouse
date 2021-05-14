package com.kepler88d.emojihouse.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kepler88d.emojihouse.MainActivity
import com.kepler88d.emojihouse.R
import com.kepler88d.emojihouse.User
import com.kepler88d.emojihouse.databinding.FragmentChatBinding
import com.kepler88d.emojihouse.url
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrPosition
import emoji4j.EmojiUtils
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper


class ChatFragment() : Fragment() {
    lateinit var binding: FragmentChatBinding
    lateinit var userData: User

    lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    var firstlaunch = true
    var roomId = ""
    private val emojiList = listOf(
        "😏", "🤣", "🤡", "😎",
        "🤥", "😉", "😳", "🧐",
        "🤓", "🤩", "🥳", "🤯",
        "🤪", "😋", "🤨", "😼",
        "🏘", "🏠", "🏚", "🏡",
        "🥶", "🥴", "👺", "🤖",
        "💩", "🐸", "🐖"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (savedInstanceState == null) {
            roomId = (requireActivity() as MainActivity).roomId
        }

        userData = User("", "", "")

        requireActivity().openFileInput("id").use {
            userData.id = it.readBytes().toString(Charsets.UTF_8)
        }

        requireActivity().openFileInput("username").use {
            userData.username = it.readBytes().toString(Charsets.UTF_8)
        }

        requireActivity().openFileInput("profileImage").use {
            userData.profileImage = it.readBytes().toString(Charsets.UTF_8)
        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat,
            container,
            false
        )

        binding.inputField.editText!!.showSoftInputOnFocus = false
        loadKeyboard()
        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        binding.imageView.setOnClickListener {
            requireActivity().onBackPressed()
        }
        fetchRoomName()
        addListenerForMessages()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.inputField.editText!!.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.inputField.editText!!.isFocusedByDefault = true

        binding.menuButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.chat_dropdown_menu)
        }
        return binding.root
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireActivity(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { true }
        popup.setOnDismissListener {}
        popup.show()
    }


    private fun fetchRoomName() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$roomId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("roomName").getValue().toString()
                binding.textView4.text = name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun addListenerForMessages() {
        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$roomId/messages")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    return
                }
                if (!firstlaunch) {
                    listOf(snapshot.children.last()).forEach {
                        val sender = it.child("sender").value
                        FirebaseDatabase.getInstance(url).getReference("/users/$sender")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    addMessage(
                                        snapshot.child("username").value.toString(),
                                        snapshot.child("profileImage").value.toString(),
                                        it.child("message").value.toString()
                                    )
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                } else {
                    snapshot.children.forEach {
                        firstlaunch = false
                        FirebaseDatabase.getInstance(url)
                            .getReference("/users/${it.child("sender").value}")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    addMessage(
                                        snapshot.child("username").value.toString(),
                                        snapshot.child("profileImage").value.toString(),
                                        it.child("message").value.toString()
                                    )
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu.add("a").setOnMenuItemClickListener { true }
        menu.add("b").setOnMenuItemClickListener { true }
    }


    private fun addMessage(username: String, icon: String, messageText: String) {
        if (messageText.trim() == "null") {
            return
        }

        val newView = LayoutInflater
            .from(requireActivity()).inflate(R.layout.message_item, null, false)

        newView.findViewWithTag<TextView>("username").text = username
        newView.findViewWithTag<TextView>("icon").text = icon
        newView.findViewWithTag<TextView>("messageText").text = messageText
        newView.findViewWithTag<TextView>("messageText").textSize =
            if (messageText.length <= 5) 48F else 24F

        binding.chatList.addView(newView)
        binding.nestedScrollView.run {
            binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
        }
        OverScrollDecoratorHelper.setUpOverScroll(binding.nestedScrollView)
    }

    private fun loadKeyboard() {
        for (i in 0 until (emojiList.size / 5)) {
            val newView =
                LayoutInflater.from(requireActivity()).inflate(R.layout.emoji_line, null, false)

            for (j in 0 until 5) {
                newView.findViewWithTag<MaterialButton>("button$j").text = emojiList[i * 5 + j]
                newView.findViewWithTag<MaterialButton>("button$j").setOnClickListener {
                    binding.inputField.editText!!.append(emojiList[i * 5 + j])
                }
            }

            binding.emojiLayout.addView(newView)
        }
    }

    private fun sendMessage() {
        val message = binding.inputField.editText!!.text.toString()
        if (message.isEmpty()) {
            return
        }

        val ref = FirebaseDatabase.getInstance(url).getReference("/rooms/$roomId/messages")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                ref.child(snapshot.childrenCount.toString())
                    .child("sender").setValue(userData.id)
                ref.child(snapshot.childrenCount.toString())
                    .child("message").setValue(message)
            }
        })

        binding.inputField.editText!!.setText("")
    }

    var slidrInterface: SlidrInterface? = null

    override fun onResume() {
        super.onResume()
        if (slidrInterface == null) {
            slidrInterface = Slidr.replace(
                binding.contentContainer,
                SlidrConfig.Builder().position(SlidrPosition.LEFT).build()
            )
        }
    }
}