package com.example.myapplication.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

class PostRegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dohvati username iz Intenta
        val newUser = intent.getStringExtra("username") ?: ""
        setContent {
            MyApplicationTheme {
                PostRegistrationScreen(newUser = newUser, onNoticeUnderstood = {
                    finish() // vraća korisnika natrag (npr. login screen ili početni ekran)
                })
            }
        }
    }
}

@Composable
fun PostRegistrationScreen(newUser: String, onNoticeUnderstood: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Uspješno ste se registrirali kao '$newUser'. " +
                        "Vaš račun je poslan na odobrenje. Hvala na čekanju!",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNoticeUnderstood) {
                Text("Razumijem")
            }
        }
    }
}
