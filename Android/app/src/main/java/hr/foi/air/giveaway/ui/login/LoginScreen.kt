package hr.foi.air.giveaway.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hr.foi.air.giveaway.viewmodel.LoginViewModel
import hr.foi.air.giveaway.viewmodel.LoginState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.loginState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Prijava", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("E-mail / Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Lozinka") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Prijavi se")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val s = state) {
            is LoginState.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))

            is LoginState.Success -> {
                Text("Prijava uspješna!", color = Color.Green)
                // kad uspije, pozovi navigaciju
                LaunchedEffect(Unit) { onLoginSuccess() }
            }

            is LoginState.Error ->
                Text(s.message, color = Color.Red)

            else -> {}
        }
    }
}
