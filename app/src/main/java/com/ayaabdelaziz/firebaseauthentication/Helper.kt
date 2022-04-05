package com.ayaabdelaziz.firebaseauthentication

import android.content.Context
import android.widget.Toast

fun Context.toast(messege: String) =
    Toast.makeText(this, messege, Toast.LENGTH_LONG).show()
