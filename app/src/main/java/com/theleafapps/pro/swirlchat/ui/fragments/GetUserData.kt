package com.theleafapps.pro.swirlchat.ui.fragments

import android.Manifest
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.theleafapps.pro.swirlchat.R
import com.theleafapps.pro.swirlchat.constants.AppConstants
import com.theleafapps.pro.swirlchat.databinding.FragmentGetUserDataBinding
import com.theleafapps.pro.swirlchat.ui.DashboardActivity
import com.theleafapps.pro.swirlchat.util.MyProgressDialog

class GetUserData : Fragment() {

    private var image: Uri? = null
    private lateinit var username: String
    private lateinit var status: String
    private lateinit var imageUrl: String
    private var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var storageReference: StorageReference? = null
    private var binding: FragmentGetUserDataBinding? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var myProgressDialog: MyProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_user_data, container, false)
        binding = FragmentGetUserDataBinding.inflate(inflater, container, false)

        myProgressDialog = MyProgressDialog(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().reference
        sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)

        binding?.btnDataDone?.setOnClickListener {
            if (checkData()) {
                MyProgressDialog.show(activity, myProgressDialog, "", "")
                uploadData(username, status, image!!)
            }
        }

        binding?.imgPickImage?.setOnClickListener {
            if (checkStoragePermission())
                pickImage()
            else storageRequestPermission()
        }
        return binding?.root
    }

    private fun checkData(): Boolean {
        username = binding?.edtUserName?.text.toString().trim()
        status = binding?.edtUserStatus?.text.toString().trim()

        return if (username.isEmpty()) {
            binding?.edtUserName?.error = getString(R.string.blank_user_name_err_msg)
            false
        } else if (status.isEmpty()) {
            binding?.edtUserStatus?.error = getString(R.string.blank_status_err_msg)
            false
        } else if (image == null) {
            Toast.makeText(
                context,
                getString(R.string.No_profile_image_err_msg), Toast.LENGTH_SHORT
            ).show()
            false
        } else true
    }

    private fun uploadData(name: String, status: String, image: Uri) = kotlin.run {
        storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH).putFile(image)
            .addOnSuccessListener {
                val task = it.storage.downloadUrl
                task.addOnCompleteListener { uri ->
                    imageUrl = uri.result.toString()
                    val map = mapOf(
                        "name" to name,
                        "status" to status,
                        "image" to imageUrl
                    )
                    databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)

                    startActivity(Intent(context, DashboardActivity::class.java))
                    requireActivity().finish()
                    myProgressDialog.dismiss()
                }
            }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 1000
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    image = result.uri
                    binding?.imgUser?.setImageURI(image)
                    val editor = sharedPreferences.edit()
                    editor.putString("myImage", image.toString()).apply()
                }
            }
        }
    }

    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }
}