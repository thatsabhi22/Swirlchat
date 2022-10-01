package com.theleafapps.pro.swirlchat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.ui.fragments.GetUserNumber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, GetUserNumber())
            .commit()
    }
}