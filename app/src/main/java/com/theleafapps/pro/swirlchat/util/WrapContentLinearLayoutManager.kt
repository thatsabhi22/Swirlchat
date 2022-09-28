package com.theleafapps.pro.swirlchat.util

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrapContentLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}