package com.theleafapps.pro.swirlchat.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theleafapps.pro.swirlchat.R

class GetUserNumber : Fragment() {

    private var number: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_user_number, container, false)
        return view
    }

    private fun checkNumber(): Boolean {
        number = view!!.edtNumber.text.toString().trim()
        if (number!!.isEmpty()) {
            view!!.edtNumber.error = "Field is required"
            return false
        } else if (number!!.length < 10) {
            view!!.edtNumber.error = "Number should be 10 in length"
            return false
        } else return true
    }
}