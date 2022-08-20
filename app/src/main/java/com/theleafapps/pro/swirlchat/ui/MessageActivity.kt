package com.theleafapps.pro.swirlchat.ui

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.ActivityMessageBinding
import com.theleafapps.pro.swirlchat.entities.MessageModel
import com.theleafapps.pro.swirlchat.permission.AppPermission
import com.theleafapps.pro.swirlchat.util.AppUtil

class MessageActivity : AppCompatActivity() {

    private lateinit var activityMessageBinding: ActivityMessageBinding
    private var hisId: String? = null
    private var hisImage: String? = null
    private var chatId: String? = null
    private var myName: String? = null
    private lateinit var appUtil: AppUtil
    private lateinit var myId: String
    private var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<MessageModel, RecyclerView.ViewHolder>? = null
    private lateinit var myImage: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var appPermission: AppPermission

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityMessageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(activityMessageBinding.root)
        appUtil = AppUtil()
        myId = appUtil.getUID()!!
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE)
        myImage = sharedPreferences.getString("myImage", "").toString()
        myName = sharedPreferences.getString("myName", "").toString()
        appPermission = AppPermission()


    }
}