package com.ayaabdelaziz.firebaseauthentication

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ayaabdelaziz.firebaseauthentication.databinding.FragmentProfilleBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfilleBinding
    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var imageUri: Uri
    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilleBinding.inflate(inflater)
        binding.imageView.setOnClickListener {
            takePictureIntent()
        }
        binding.textPhone.setOnClickListener {
            findNavController().navigate(R.id.addPhoneFragment)
        }
        binding.buttonSave.setOnClickListener {
            val photo = when {
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }
            val name = binding.editTextName.text.toString().trim()
            if (name.isEmpty()) {
                binding.editTextName.error = "Name Reguired"
                binding.editTextName.requestFocus()
            }
            if (currentUser!!.isEmailVerified) {
                binding.textNotVerified.visibility = View.VISIBLE
            } else {
                binding.textNotVerified.visibility = View.INVISIBLE
            }
            val update =
                UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photo).build()
            binding.progressbar.visibility = View.VISIBLE
            currentUser!!.updateProfile(update).addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.progressbar.visibility = View.GONE
                    activity?.toast("Profile Updated")
                } else {
                    activity?.toast(it.exception?.message!!)
                }
            }

        }
        binding.textNotVerified.setOnClickListener {
            currentUser!!.sendEmailVerification().addOnCompleteListener {
                if (it.isSuccessful) {
                    context!!.toast("Verification succssed")
                } else {
                    context!!.toast(it.exception!!.message!!)
                }
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUser?.let {
            Glide.with(this).load(it.photoUrl.toString()).into(binding.imageView)
            binding.editTextName.setText(it.displayName)
            binding.textEmail.setText(it.email)
            binding.textPhone.text =
                if (it.phoneNumber.isNullOrEmpty()) "Add Number" else it.phoneNumber
        }
    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!).also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImagewithUri(imageBitmap)
        }
    }
    private fun uploadImagewithUri(imageBitmap: Bitmap) {
        val boas = ByteArrayOutputStream()
        val storageRef =
            FirebaseStorage.getInstance().reference.child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas)
        val image = boas.toByteArray()
        val upload = storageRef.putBytes(image)
        binding.progressbarPic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            if (uploadTask.isSuccessful) {
                binding.progressbarPic.visibility = View.GONE
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    urlTask.result?.let {
                        imageUri = it
//                        activity?.toast(imageUri.toString())
                        binding.imageView.setImageBitmap(imageBitmap)
                    }
                }
            } else {
                activity?.toast(uploadTask.exception?.message!!)
            }
        }
    }
}