package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.ui.theme.MyApplicationTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Lunsiraj Composable sa callbackom za uspjeh registracije
                RegistrationScreen(onSuccessfulRegistration = { userId, username ->
                    // Spremi userId u SharedPreferences
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit().putInt("userId", userId).apply()
                    // Pokreni PostRegistrationActivity s proslijeđenim usernameom
                    val intent = Intent(this, PostRegistrationActivity::class.java).apply {
                        putExtra("username", username)
                    }
                    startActivity(intent)
                    finish()  // završava RegistrationActivity
                })
            }
        }
    }
}
