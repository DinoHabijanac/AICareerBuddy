package com.example.myapplication.activities

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.LoginRequest
import com.example.core.models.RegistrationRequest
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.oauth.GoogleLogin
import com.example.oauth.GoogleLoginViewModel

var userEmail: String = ""
var firstName: String = ""
var lastName: String = ""
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    googleViewModel: GoogleLoginViewModel = viewModel(),
    onSuccessfulLogin: (userId: Int, username: String) -> Unit,
) {
    val username by viewModel.username.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val errorMsg by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)

    val statusGoogle by googleViewModel.status.observeAsState()
    val statusRegGoogle by googleViewModel.statusReg.observeAsState()
    val googleUserId by googleViewModel.userId.observeAsState()
    val googleUsername by googleViewModel.username.observeAsState()
    val webClientId = "66031714200-bcubnq7smv4mjhgl8jsg3p2c9k3t6hr1.apps.googleusercontent.com"
    var google by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var registrationPassword by remember { mutableStateOf("") }
    var registrationErrorMsg by remember { mutableStateOf("") }
    val context = LocalContext.current
    val onLoginSuccess by rememberUpdatedState(onSuccessfulLogin)

    // Resetiraj registracijske varijable kada se dialog zatvori
    LaunchedEffect(showRegisterDialog) {
        if (!showRegisterDialog) {
            registrationPassword = ""
            registrationErrorMsg = ""
        }
    }

    if (google) {
        LaunchedEffect(Unit) {
            val result = GoogleLogin.signInWithFallback(context, webClientId)
            val profile = result.profile
            if (profile != null && profile.email.isNotEmpty()) {
                userEmail = profile.email
                firstName = profile.firstName
                lastName = profile.lastName

                val loginRequest = LoginRequest(username = userEmail.substringBefore("@"), password = "")
                googleViewModel.loginUserWithGoogle(loginRequest) { success ->
                    Log.d("LoginResult", "Success: $success")
                }
            } else {
                Log.e("GoogleLogin", "Prijava putem Google-a nije uspjela", result.exception)
                Toast.makeText(context, "Prijava putem Google-a nije uspjela", Toast.LENGTH_SHORT).show()
            }
            google = false
        }
    }

    LaunchedEffect(statusGoogle) {
        if (statusGoogle == "200") {
            try {
                onLoginSuccess(googleUserId ?: -1, googleUsername ?: userEmail.substringBefore("@"))
            } catch (t: Throwable) {
                Log.e("LoginScreen", "onSuccessfulLogin callback greška", t)
            }
        } else if (statusGoogle == "404") {
            Toast.makeText(context, "Korisnik nije pronađen. Registriraj se.", Toast.LENGTH_LONG).show()
            showRegisterDialog = true
        }
    }

    LaunchedEffect(statusRegGoogle) {
        if (statusRegGoogle == "200" && !showRegisterDialog) {
            try {
                onLoginSuccess(googleUserId ?: -1, googleUsername ?: userEmail.substringBefore("@"))
            } catch (t: Throwable) {
                Log.e("LoginScreen", "onSuccessfulLogin callback nakon rege greška", t)
            }
        }
    }

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = {
                showRegisterDialog = false
            },
            title = { Text("Dovrši registraciju") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val fieldColors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )

                    OutlinedTextField(
                        value = userEmail.substringBefore("@"),
                        onValueChange = { },
                        label = { Text("Korisničko ime") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = fieldColors
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = registrationPassword,
                        onValueChange = { newValue -> registrationPassword = newValue },
                        label = { Text("Lozinka") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = fieldColors
                    )

                    if (registrationErrorMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(registrationErrorMsg, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (registrationPassword.isBlank()) {
                            registrationErrorMsg = "Lozinka je obavezna"
                            return@Button
                        }

                        val regRequest = RegistrationRequest(
                            email = userEmail,
                            username = userEmail.substringBefore("@"),
                            firstName = firstName,
                            lastName = lastName,
                            password = registrationPassword,
                            role = "student"
                        )

                        googleViewModel.registerGoogle(regRequest) { success ->
                            if (success) {
                                showRegisterDialog = false
                                registrationPassword = ""
                                // onLoginSuccess će biti pozvan automatski kroz LaunchedEffect(statusRegGoogle)
                            } else {
                                registrationErrorMsg = statusRegGoogle ?: "Greška pri registraciji"
                            }
                        }
                    }
                ) {
                    Text("Registriraj se")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showRegisterDialog = false
                    }
                ) {
                    Text("Otkazi")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Prijava korisnika",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val fieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
            OutlinedTextField(
                value = username ?: "",
                onValueChange = { viewModel.username.value = it },
                label = { Text("Korisničko ime") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password ?: "",
                onValueChange = { viewModel.password.value = it },
                label = { Text("Lozinka") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.loginUser(
                        onSuccess = { id, user ->
                            onSuccessfulLogin(id, user)
                        },
                        onFail = { }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Dohvaćam..." else "Prijava")
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    google = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google prijava")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun callPreview() {
    LoginScreen(onSuccessfulLogin = { _, _ -> })
}
