package com.theleafapps.pro.swirlchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.ActivityDashboardBinding
import com.theleafapps.pro.swirlchat.ui.fragments.ChatFragment
import com.theleafapps.pro.swirlchat.util.AppUtil

class DashboardActivity : AppCompatActivity() {

    private var fragment: Fragment? = null
    private var dashboardBinding: ActivityDashboardBinding? = null
    private lateinit var appUtil: AppUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding?.root)
        setContentView(R.layout.activity_dashboard)
        appUtil = AppUtil()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment()).commit()
            dashboardBinding?.bottomChip?.setItemSelected(R.id.btnChat)
        }

        dashboardBinding?.bottomChip?.setOnItemSelectedListener { id ->
            when (id) {
                R.id.btnChat -> {
                    fragment = ChatFragment()
                }

                R.id.btnProfile -> {
                    fragment = ProfileFragment();
                }
                R.id.btnContact -> fragment = ContactFragment()
            }

            fragment!!.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment!!)
                    .commit()
            }
        }
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