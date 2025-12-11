// LoginActivity.kt
package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.ui.theme.MyApplicationTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                LoginScreen(onSuccessfulLogin = { userId, username ->

                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putInt("userId", userId)
                        .putString("username", username)
                        .apply()

                    // Preusmjeri na HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}
