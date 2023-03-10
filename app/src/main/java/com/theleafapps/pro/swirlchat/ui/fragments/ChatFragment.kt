package com.theleafapps.pro.swirlchat.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.ChatItemLayoutBinding
import com.theleafapps.pro.swirlchat.databinding.FragmentChatBinding
import com.theleafapps.pro.swirlchat.entities.ChatListModel
import com.theleafapps.pro.swirlchat.entities.ChatModel
import com.theleafapps.pro.swirlchat.entities.UserModel
import com.theleafapps.pro.swirlchat.ui.MessageActivity
import com.theleafapps.pro.swirlchat.util.AppUtil
import com.theleafapps.pro.swirlchat.util.WrapContentLinearLayoutManager

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var appUtil: AppUtil
    private lateinit var firebaseRecyclerAdapter:
            FirebaseRecyclerAdapter<ChatListModel, ViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        appUtil = AppUtil()
        readChat()
        return binding.root
    }

    private fun readChat() {

        val query =
            FirebaseDatabase.getInstance().getReference("ChatList").child(appUtil.getUID()!!)
        val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<ChatListModel>()
            .setLifecycleOwner(this)
            .setQuery(query, ChatListModel::class.java)
            .build()
        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ChatListModel, ViewHolder>(firebaseRecyclerOptions) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    val chatItemLayoutBinding: ChatItemLayoutBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.chat_item_layout, parent, false
                    )

                    return ViewHolder(chatItemLayoutBinding)
                }

                override fun onBindViewHolder(
                    holder: ViewHolder,
                    p1: Int,
                    chatListModel: ChatListModel
                ) {
                    val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(chatListModel.member)
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val userModel = snapshot.getValue(UserModel::class.java)
                                val date = appUtil.getTimeAgo(chatListModel.date.toLong())

                                val chatModel = ChatModel(
                                    chatListModel.chatId,
                                    userModel?.name,
                                    chatListModel.lastMessage,
                                    userModel?.image,
                                    date,
                                    userModel?.online
                                )

                                holder.chatItemLayoutBinding.chatModel = chatModel

                                holder.itemView.setOnClickListener {
                                    val intent = Intent(context, MessageActivity::class.java)
                                    intent.putExtra("hisId", userModel?.uid)
                                    intent.putExtra("hisImage", userModel?.image)
                                    intent.putExtra("chatId", chatListModel.chatId)
                                    intent.putExtra("hisName", userModel?.name)
                                    startActivity(intent)
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }

        binding.recyclerViewChat.layoutManager = WrapContentLinearLayoutManager(context)
        binding.recyclerViewChat.setHasFixedSize(false)
        binding.recyclerViewChat.adapter = firebaseRecyclerAdapter
    }

    class ViewHolder(val chatItemLayoutBinding: ChatItemLayoutBinding) :
        RecyclerView.ViewHolder(chatItemLayoutBinding.root)

    override fun onResume() {
        super.onResume()
        firebaseRecyclerAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        firebaseRecyclerAdapter.stopListening()
    }
}