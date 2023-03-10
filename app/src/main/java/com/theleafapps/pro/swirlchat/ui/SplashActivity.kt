package com.theleafapps.pro.swirlchat.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.util.AppUtil

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appUtil: AppUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(this);

        firebaseAuth = Firebase.auth
        appUtil = AppUtil()

        Handler().postDelayed({

            if (firebaseAuth.currentUser == null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {

                FirebaseInstallations.getInstance().getToken(false)
                    .addOnCompleteListener(OnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result?.token
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(appUtil.getUID()!!)

                            val map: MutableMap<String, Any> = HashMap()
                            map["token"] = token!!
                            databaseReference.updateChildren(map)
                        }
                    })
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }

        }, 3000)
    }
}