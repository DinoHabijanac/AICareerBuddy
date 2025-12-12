package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ako je logiran, odmah routing (i prekid)
        if (routeIfLoggedIn()) return

        // Ako nije logiran -> landing (2 gumba)
        setContent {
            MyApplicationTheme {
                AuthLandingScreen(
                    onRegisterClick = {
                        startActivity(Intent(this, RegistrationActivity::class.java))
                    },
                    onLoginClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Bitno: kad se vratiš iz LoginActivity, onCreate se možda neće zvati opet,
        // ali onResume hoće -> zato ovdje ponovo provjerimo.
        routeIfLoggedIn()
    }

    /**
     * Vrati true ako je user logiran i napravljen je redirect u MainActivity.
     * Vrati false ako nije logiran.
     */
    private fun routeIfLoggedIn(): Boolean {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val username = prefs.getString("username", null)

        val isLoggedIn = userId != -1

        Log.d("HOME_PREFS", "userId=$userId username=$username isLoggedIn=$isLoggedIn")

        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)
            finish()
            return true
        }
        return false
    }
}

@Composable
private fun AuthLandingScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "AI Career Buddy", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRegisterClick, modifier = Modifier.width(220.dp)) {
            Text("Registracija")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginClick, modifier = Modifier.width(220.dp)) {
            Text("Prijava")
        }
    }
}
