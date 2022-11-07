package com.theleafapps.pro.swirlchat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.ActivityDashboardBinding
import com.theleafapps.pro.swirlchat.ui.fragments.ChatFragment
import com.theleafapps.pro.swirlchat.ui.fragments.ContactFragment
import com.theleafapps.pro.swirlchat.ui.fragments.ProfileFragment
import com.theleafapps.pro.swirlchat.util.AppUtil
import com.theleafapps.pro.swirlchat.util.ConnectivityLiveData

class DashboardActivity : AppCompatActivity() {

    private var fragment: Fragment? = null
    private lateinit var mBottomSheetLayout: ConstraintLayout
    private lateinit var dashboardBinding: ActivityDashboardBinding
    private lateinit var appUtil: AppUtil
    private lateinit var connectivityLiveData: ConnectivityLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashboardBinding.root)
        appUtil = AppUtil()
        connectivityLiveData = ConnectivityLiveData(application)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.dashboardContainer, ChatFragment()).commit()
        }

        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.no_internet_dialog_layout, null)

        connectivityLiveData.observe(this, Observer { isAvailable ->
            when (isAvailable) {
                true -> {
                    dialog.setCancelable(true)
                    dialog.dismiss()
                }
                false -> {
                    dialog.setCancelable(false)
                    dialog.setContentView(view)
                    dialog.show()
                }
            }
        })

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