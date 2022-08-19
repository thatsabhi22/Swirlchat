package com.theleafapps.pro.swirlchat.ui.fragments

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.FragmentContactBinding
import com.theleafapps.pro.swirlchat.entities.UserModel

class ContactFragment : Fragment() {

    private lateinit var appPermission: AppPermission
    private lateinit var fragmentContactBinding: FragmentContactBinding
    private lateinit var mobileContacts: ArrayList<UserModel>
    private lateinit var appContacts: ArrayList<UserModel>
    private var contactAdapter: ContactAdapter? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContactBinding = FragmentContactBinding.inflate(inflater, container, false)
        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        phoneNumber = firebaseAuth.currentUser?.displayName!!

        if (appPermission.isContactOk(requireContext())) {
            getMobileContact()
        } else appPermission.requestContactPermission(requireActivity())

        fragmentContactBinding.contactSearchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (contactAdapter != null)
                    contactAdapter!!.filter.filter(newText)
                return false
            }
        })
        return fragmentContactBinding.root
    }

}