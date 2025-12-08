// app/src/main/java/com/example/myapplication/activities/LoginActivity.kt
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
                // Prikaz LoginScreen Composable
                LoginScreen(onSuccessfulLogin = { userId, username ->
                    // Spremi userId u SharedPreferences (označavanje da je korisnik prijavljen)
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit().putInt("userId", userId).apply()
                    // (Opcionalno: možemo spremiti i username ako je potrebno)
                    // prefs.edit().putString("username", username).apply()

                    // Povratak na glavni ekran
                    finish()  // zatvori LoginActivity i vrati se na MainActivity
                })
            }
        }
    }
}
