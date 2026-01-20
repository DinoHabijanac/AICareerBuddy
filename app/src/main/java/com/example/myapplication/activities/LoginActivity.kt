// LoginActivity.kt
package com.example.myapplication.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webClientId = "66031714200-bcubnq7smv4mjhgl8jsg3p2c9k3t6hr1.apps.googleusercontent.com"

        setContent {
            MyApplicationTheme {
                LoginScreen(onSuccessfulLogin = { userId, username ->
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putInt("userId", userId)
                        .putString("username", username)
                        .apply()

                    // HomeActivity je "router": ako je logiran, ona će prebaciti dalje (na MainActivity / kasnije dev-android)
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}
