package com.theleafapps.pro.swirlchat.ui.fragments

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theleafapps.pro.swirlchat.adapters.ContactAdapter
import com.theleafapps.pro.swirlchat.databinding.FragmentContactBinding
import com.theleafapps.pro.swirlchat.entities.UserModel
import com.theleafapps.pro.swirlchat.permission.AppPermission
import com.theleafapps.pro.swirlchat.util.WrapContentLinearLayoutManager

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
        phoneNumber = firebaseAuth.currentUser?.phoneNumber!!

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

    private fun getMobileContact() {

        val projection = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            mobileContacts = ArrayList()
            while (cursor.moveToNext()) {

                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

                number = number.replace("\\s".toRegex(), "")
                val num = number.elementAt(0).toString()
                if (num == "0")
                    number = number.replaceFirst("(?:0)+".toRegex(), "+91")
                val userModel = UserModel()
                userModel.name = name
                userModel.number = number
                mobileContacts.add(userModel)
            }
            cursor.close()
            getAppContact(mobileContacts)
        }
    }

    private fun getAppContact(mobileContact: ArrayList<UserModel>) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference.orderByChild("number")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appContacts = ArrayList()
                    for (data in snapshot.children) {

                        val number = data.child("number").value.toString()
                        for (mobileModel in mobileContact) {
                            if (mobileModel.number == number && number != phoneNumber) {
                                val userModel = data.getValue(UserModel::class.java)
                                appContacts.add(userModel!!)
                            }
                        }
                    }

                    fragmentContactBinding.recyclerViewContact.apply {
                        layoutManager = WrapContentLinearLayoutManager(context)
                        setHasFixedSize(true)
                        contactAdapter = ContactAdapter(appContacts)
                        adapter = contactAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}