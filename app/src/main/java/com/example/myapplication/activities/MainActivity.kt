// app/src/main/java/com/example/myapplication/activities/MainActivity.kt
package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PROČITAJ PODATKE O PRIJAVLJENOM KORISNIKU
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = prefs.getInt("userId", -1)
        val username = prefs.getString("username", null)
        val isLoggedIn = userId != -1

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        isLoggedIn = isLoggedIn,
                        loggedInUsername = username,
                        onRegisterClick = {
                            // Otvori ekran za registraciju
                            startActivity(Intent(this, RegistrationActivity::class.java))
                        },
                        onLoginClick = {
                            // Otvori ekran za prijavu
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = false,
    loggedInUsername: String? = null,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zaglavlje sa naslovom aplikacije i logom (plava traka)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Blue)
                .height(80.dp)
        ) {
            Text(
                text = "AI Career Buddy",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Ako je korisnik prijavljen, prikaži informaciju
        if (isLoggedIn && !loggedInUsername.isNullOrBlank()) {
            Text(
                text = "Prijavljeni korisnik: $loggedInUsername",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Gumbi za odabir opcije
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Odaberi opciju", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            // Gumb za registraciju
            Button(onClick = onRegisterClick, modifier = Modifier.width(220.dp)) {
                Text(text = "Registracija")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Gumb za prijavu
            Button(onClick = onLoginClick, modifier = Modifier.width(220.dp)) {
                Text(text = "Prijava")
            }
        }
    }
}
