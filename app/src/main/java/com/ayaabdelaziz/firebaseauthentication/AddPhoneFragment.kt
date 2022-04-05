package com.ayaabdelaziz.firebaseauthentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ayaabdelaziz.firebaseauthentication.databinding.FragmentAddPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class AddPhoneFragment : Fragment() {

    private lateinit var binding: FragmentAddPhoneBinding
    private lateinit var credintial: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPhoneBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutPhone.visibility = View.VISIBLE
        binding.layoutVerification.visibility = View.GONE
        binding.buttonSendVerification.setOnClickListener {
            val phoneNumber = binding.editTextPhone.text.toString().trim()
            if (phoneNumber.isEmpty() || phoneNumber.length != 10) {
                binding.editTextPhone.error = "valid Phone required"
                binding.editTextPhone.requestFocus()
                return@setOnClickListener
            }
            val number = '+' + binding.ccp.selectedCountryCode + phoneNumber

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(number, 60, TimeUnit.SECONDS, requireActivity(), callbackreference)
            binding.layoutPhone.visibility = View.GONE
            binding.layoutVerification.visibility = View.VISIBLE
        }

        binding.buttonVerify.setOnClickListener {
            val code = binding.editTextCode.text.toString()
            if(code.isEmpty()){
                binding.editTextPhone.error = "Enater valid code"
                binding.editTextPhone.requestFocus()
                return@setOnClickListener
            }
            credintial?.let{
                val credintialVerified = PhoneAuthProvider.getCredential(it,code)
                addPhoneNumber(credintialVerified)
            }
        }
    }
    private val callbackreference = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            p0?.let {
                addPhoneNumber(it)
            }

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            activity?.toast(p0.message.toString())
        }

        override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, p1)
            credintial = verificationId
        }

    }



    private fun addPhoneNumber(it: PhoneAuthCredential) {
        FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(it)?.addOnCompleteListener {task ->
            if(task.isSuccessful){
                activity?.toast("Phone Added")
                findNavController().navigate(R.id.profileFragment)
            }else{
                activity?.toast(task.exception?.message!!)
            }

        }

    }

}