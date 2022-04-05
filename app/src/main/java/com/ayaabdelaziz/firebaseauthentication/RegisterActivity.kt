package com.ayaabdelaziz.firebaseauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import com.ayaabdelaziz.firebaseauthentication.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.log.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.signUpBtn.setOnClickListener {
            binding.let {
                val username = it.userName.text.toString()
                val _email = it.email.text.toString()
                val _password = it.pasword.text.toString()
                val _passConfirm = it.paswordConfirm.text.toString()


                if (username.isEmpty()) {
                    binding.userName.error = "User Name Required"
                    binding.userName.requestFocus()
                }
                if (_email.isEmpty()) {
                    binding.email.error = "Email Required"
                    binding.email.requestFocus()
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
                    binding.email.error = "valid Email Required"
                    binding.email.requestFocus()
                }
                if (_password.isEmpty() && _password.length < 6) {
                    binding.pasword.error = "Password Required"
                    binding.pasword.requestFocus()
                }
                if (_passConfirm.isEmpty() && !_passConfirm.equals(_password)) {
                    binding.pasword.error = "Password doesnot match"
                    binding.pasword.requestFocus()
                }
                registerUser(_email, _password)
            }


        }


    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("you Register succesfully")
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)

            } else {
                task.exception?.message?.let {
                    toast(it)
                }
            }

        }

    }
}