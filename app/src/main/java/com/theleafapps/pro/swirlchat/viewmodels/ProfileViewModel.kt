package com.theleafapps.pro.swirlchat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.theleafapps.pro.swirlchat.repository.AppRepo
import com.theleafapps.pro.swirlchat.entities.UserModel

class ProfileViewModel : ViewModel() {

    private var appRepo = AppRepo.StaticFunction.getInstance()

    fun getUser(): LiveData<UserModel> {
        return appRepo.getUser()
    }

    fun updateStatus(status: String) {
        appRepo.updateStatus(status)

    }

    fun updateName(userName: String?) {
        appRepo.updateName(userName!!)
    }

    fun updateImage(imagePath: String) {
        appRepo.updateImage(imagePath)
    }

}