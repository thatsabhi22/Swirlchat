package com.theleafapps.pro.swirlchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.databinding.FragmentGetUserNumberBinding
import com.theleafapps.pro.swirlchat.entities.UserModel
import java.util.concurrent.TimeUnit

class GetUserNumber : Fragment() {

    private var number: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var code: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var binding: FragmentGetUserNumberBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_user_number, container, false)
        binding = FragmentGetUserNumberBinding.inflate(inflater,container,false)

        firebaseAuth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding?.btnGenerateOTP?.setOnClickListener {
            if (checkNumber()) {
                val phoneNumber = binding?.countryCodePicker?.selectedCountryCodeWithPlus + number
                sendCode(phoneNumber)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userModel =
                            UserModel(
                                "", "", "",
                                firebaseAuth!!.currentUser!!.phoneNumber!!,
                                firebaseAuth!!.uid!!
                            )

                        databaseReference!!.child(firebaseAuth!!.uid!!).setValue(userModel)
                        activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.main_container, GetUserData())
                            ?.commit()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                when(e){
                    is FirebaseAuthInvalidCredentialsException ->
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    is FirebaseTooManyRequestsException ->
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    else ->
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(
                verificationCode: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                code = verificationCode
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, VerifyNumber.newInstance(code!!))
                    .commit()
            }
        }
        return binding?.root
    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())   // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun checkNumber(): Boolean {
        number = binding?.edtNumber?.text.toString().trim()
        number = binding?.edtNumber?.text.toString().trim()
        return if (number!!.isEmpty()) {
            binding?.edtNumber?.error = "Field is required"
            binding?.edtNumber?.error = "Field is required"
            false
        } else if (number!!.length < 10) {
            binding?.edtNumber?.error = "Number should be 10 in length"
            binding?.edtNumber?.error = "Number should be 10 in length"
            false
        } else true
    }
}
