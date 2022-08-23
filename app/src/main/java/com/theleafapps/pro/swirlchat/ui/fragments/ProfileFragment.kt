package com.theleafapps.pro.swirlchat.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.FragmentProfileBinding
import com.theleafapps.pro.swirlchat.permission.AppPermission

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var appPermission: AppPermission
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)

        return profileBinding.root
    }

}