package com.theleafapps.pro.swirlchat.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.FragmentVerifyNumberBinding
import com.theleafapps.pro.swirlchat.entities.UserModel
import com.theleafapps.pro.swirlchat.util.MyProgressDialog

class VerifyNumber : Fragment() {

    private var code: String? = null
    private lateinit var pin: String
    private var firebaseAuth: FirebaseAuth? = null
    private var databaseReference: DatabaseReference? = null
    private var binding: FragmentVerifyNumberBinding? = null
    private lateinit var myProgressDialog: MyProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("Code")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_verify_number, container, false)
        binding = FragmentVerifyNumberBinding.inflate(inflater, container, false)

        myProgressDialog = MyProgressDialog(activity)
        firebaseAuth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding?.btnVerify?.setOnClickListener {
            if (checkPin()) {
                MyProgressDialog.show(activity, myProgressDialog, "", "")
                val credential = PhoneAuthProvider.getCredential(code!!, pin)
                signInUser(credential)
            }
        }
        return binding?.root
    }

    private fun signInUser(credential: PhoneAuthCredential) {
        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val userModel =
                    UserModel(
                        "", "", "",
                        firebaseAuth!!.currentUser!!.phoneNumber!!,
                        firebaseAuth!!.uid!!
                    )

                databaseReference!!.child(firebaseAuth?.uid!!).setValue(userModel)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, GetUserData())
                    .commit()
                myProgressDialog.dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(code: String) =
            VerifyNumber().apply {
                arguments = Bundle().apply {
                    putString("Code", code)
                }
            }
    }

    private fun checkPin(): Boolean {
        pin = binding?.otpTextView?.text.toString()
        if (pin.isEmpty()) {
            binding?.otpTextView?.error = "Filed is required"
            return false
        } else if (pin.length < 6) {
            binding?.otpTextView?.error = "Enter valid pin"
            return false
        } else return true
    }
}