package com.ayaabdelaziz.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.ayaabdelaziz.firebaseauthentication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSignIn.setOnClickListener {
            val _email = binding.email.text.toString()
            val _password = binding.pass.text.toString()
            if (_email.isEmpty()) {
                binding.email.error = "User Name Required"
                binding.email.requestFocus()
            }
            if (_password.isEmpty()) {
                binding.pass.error = "Email Required"
                binding.pass.requestFocus()
            }
            signInUser(_email, _password)
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                toast("Sign In successfully")
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } else {
                it.exception?.message?.let {
                    toast(it)
                }

            }
        }

    }
}