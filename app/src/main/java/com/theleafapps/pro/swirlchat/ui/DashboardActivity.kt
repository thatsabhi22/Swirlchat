package com.theleafapps.pro.swirlchat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.ActivityDashboardBinding
import com.theleafapps.pro.swirlchat.ui.fragments.ChatFragment
import com.theleafapps.pro.swirlchat.ui.fragments.ContactFragment
import com.theleafapps.pro.swirlchat.ui.fragments.ProfileFragment
import com.theleafapps.pro.swirlchat.util.AppUtil

class DashboardActivity : AppCompatActivity() {

    private var fragment: Fragment? = null
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)
        appUtil = AppUtil()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment()).commit()
        }

        dashboardBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btnChat -> replaceFragment(ChatFragment())
                R.id.btnProfile -> replaceFragment(ProfileFragment())
                R.id.btnContact -> replaceFragment(ContactFragment())
                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.dashboardContainer, fragment)
            .commit()
    }

    override fun onPause() {
        super.onPause()
        appUtil.updateOnlineStatus("offline")
    }

    override fun onResume() {
        super.onResume()
        appUtil.updateOnlineStatus("online")
    }
}