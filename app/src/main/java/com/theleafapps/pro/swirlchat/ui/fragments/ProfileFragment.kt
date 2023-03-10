package com.theleafapps.pro.swirlchat.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.constants.AppConstants
import com.theleafapps.pro.swirlchat.databinding.DialogLayoutBinding
import com.theleafapps.pro.swirlchat.databinding.FragmentProfileBinding
import com.theleafapps.pro.swirlchat.permission.AppPermission
import com.theleafapps.pro.swirlchat.ui.EditNameActivity
import com.theleafapps.pro.swirlchat.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var dialogLayoutBinding: DialogLayoutBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var appPermission: AppPermission
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        profileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)

        profileViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
                .create(ProfileViewModel::class.java)

        profileViewModel.getUser().observe(viewLifecycleOwner, Observer { userModel ->
            profileBinding.userModel = userModel

            if (userModel.name.contains(" ")) {
                val split = userModel.name.split(" ")

                profileBinding.txtProfileFName.text = split[0]
                profileBinding.txtProfileLName.text = split[1]
            }

            profileBinding.editName.setOnClickListener {
                val intent = Intent(context, EditNameActivity::class.java)
                intent.putExtra("name", userModel.name)
                startActivityForResult(intent, 100)
            }
        })

        profileBinding.imgPickImage.setOnClickListener {
            if (appPermission.isStorageOk(requireContext())) {
                pickImage()
            } else appPermission.requestStoragePermission(requireActivity())
        }

        profileBinding.imgEditStatus.setOnClickListener {
            getStatusDialog()
        }

        return profileBinding.root
    }

    private fun pickImage() {
        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }

    private fun getStatusDialog() {

        val alertDialog = AlertDialog.Builder(context)
        dialogLayoutBinding = DialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        alertDialog.setView(dialogLayoutBinding.root)

        dialogLayoutBinding.btnEditStatus.setOnClickListener {
            val status = dialogLayoutBinding.edtUserStatus.text.toString()
            if (status.isNotEmpty()) {
                profileViewModel.updateStatus(status)
                dialog.dismiss()
            }
        }
        dialog = alertDialog.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (data != null) {
                    val userName = data.getStringExtra("name")
                    profileViewModel.updateName(userName!!)
                    val editor = sharedPreferences.edit()
                    editor.putString("myName", userName).apply()
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (data != null) {

                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        uploadImage(result.uri)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.STORAGE_PERMISSION -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) pickImage()
                else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {

        storageReference = FirebaseStorage.getInstance().reference
        storageReference.child(firebaseAuth.uid + AppConstants.PATH).putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                val task = taskSnapshot.storage.downloadUrl
                task.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val imagePath = it.result.toString()

                        val editor = sharedPreferences.edit()
                        editor.putString("myImage", imagePath).apply()

                        profileViewModel.updateImage(imagePath)
                    }
                }
            }
    }
}