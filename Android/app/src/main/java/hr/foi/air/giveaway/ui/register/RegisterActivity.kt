package hr.foi.air.giveaway.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.giveaway.MainActivity
import hr.foi.air.giveaway.ui.theme.AICareerBuddyTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Omogućuje prikaz preko cijelog ekrana (Edge-to-Edge)
        enableEdgeToEdge()
        // Postavljanje Compose sadržaja
        setContent {
            AICareerBuddyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Pozivamo glavni Composable za ekran registracije
                    RegisterScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(modifier: Modifier = Modifier, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    val usernameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val registerState by viewModel.registerState.collectAsState()

    if (registerState is RegisterState.Success) {
        LaunchedEffect(registerState) {
             context.startActivity(Intent(context, MainActivity::class.java))
             (context as? Activity)?.finish()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),  // rubni razmak oko forme
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Registracija",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            label = { Text(text = "Korisničko ime") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
       OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text(text = "Email") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text(text = "Lozinka") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
         OutlinedTextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text(text = "Potvrda lozinke") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.registerUser(
                    usernameState.value,
                    emailState.value,
                    passwordState.value,
                    confirmPasswordState.value
                )
            },
            enabled = registerState !is RegisterState.Loading  // onemogući gumb ako je slanje u tijeku
        ) {
            Text(text = "Registriraj se")
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (registerState) {
            is RegisterState.Loading -> {
                Text(text = "Registracija u tijeku...", color = androidx.compose.ui.graphics.Color.Gray)
            }
            is RegisterState.Success -> {
                Text(
                    text = (registerState as RegisterState.Success).message,
                    color = androidx.compose.ui.graphics.Color.Green
                )
            }
            is RegisterState.Error -> {
                Text(
                    text = (registerState as RegisterState.Error).message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            else -> { /* Idle state - ne prikazujemo nikakvu poruku */ }
        }
    }
}
