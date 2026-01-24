
package com.example.myapplication.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.credentials.GetCredentialException
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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.LoginRequest
import com.example.core.models.RegistrationRequest
import com.example.myapplication.R
import com.example.myapplication.viewmodels.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.delay
import java.security.SecureRandom
import java.util.Base64


var userEmail: String = ""
var firstName: String = ""
var lastName: String = ""

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onSuccessfulLogin: (userId: Int, username: String) -> Unit,
) {
    val username by viewModel.username.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val errorMsg by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)

    val status by viewModel.status.observeAsState()
    val statusReg by viewModel.statusReg.observeAsState()
    val id by viewModel.userId.observeAsState()
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
        //// modificirana verzija koda za prijavu google-om - preuzet sa: https://codelabs.developers.google.com/sign-in-with-google-android#5
        LaunchedEffect(Unit) {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setNonce(generateSecureRandomNonce())
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            var exception: Exception? = signIn(request, context)

            Log.d("Usermail", userEmail)
            if (userEmail.isNotEmpty()) {
                val loginRequest = LoginRequest(username = userEmail.substringBefore("@"), "")
                Log.d("Ovo", loginRequest.username)
                viewModel.loginUserWithGoogle(loginRequest) { success ->
                    Log.d("LoginResult", "Success: $success")
                }
            } else {
                if (exception is NoCredentialException) {
                    val googleIdOptionFalse: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setNonce(generateSecureRandomNonce())
                        .build()

                    val requestFalse: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOptionFalse)
                        .build()

                    exception = signIn(requestFalse, context)

                    if (userEmail.isNotEmpty()) {
                        val loginRequest = LoginRequest(username = userEmail.substringBefore("@"), "")
                        Log.d("ovo", loginRequest.username)
                        viewModel.loginUserWithGoogle(loginRequest) { success ->
                            Log.d("LoginResult", "Success: $success")
                        }
                    }
                }
            }

            google = false
        }
    }

     LaunchedEffect(status) {
        if (status == "200") {
            try {
                onLoginSuccess(id ?: -1, username ?: "")
            } catch (t: Throwable) {
                Log.e("LoginScreen", "onSuccessfulLogin callback greška", t)
            }
        } else if (status == "404") {
            Toast.makeText(context, "Korisnik nije pronađen. Registriraj se.", Toast.LENGTH_LONG).show()
            showRegisterDialog = true
        }
    }

    LaunchedEffect(statusReg) {
        if (statusReg == "200" && !showRegisterDialog) {
            try {
                onLoginSuccess(id ?: -1, username ?: "")
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

                        viewModel.registerGoogle(regRequest) { success ->
                            if (success) {
                                showRegisterDialog = false
                                registrationPassword = ""
                                // onLoginSuccess će biti pozvan automatski kroz LaunchedEffect(statusReg)
                            } else {
                                registrationErrorMsg = statusReg ?: "Greška pri registraciji"
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

////modificirana verzija koda za prijavu google-om - preuzet sa: https://codelabs.developers.google.com/sign-in-with-google-android#5

fun generateSecureRandomNonce(byteLength: Int = 32): String {
    val randomBytes = ByteArray(byteLength)
    SecureRandom.getInstanceStrong().nextBytes(randomBytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun signIn(request: GetCredentialRequest, context: Context): Exception? {
    val credentialManager = CredentialManager.create(context)
    val failureMessage = "Neuspješna prijava!"
    var exception: Exception? = null

    delay(250)
    try {
        val result = credentialManager.getCredential(
            request = request,
            context = context,
        )
        userEmail = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID").toString()
        firstName = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_GIVEN_NAME").toString()
        lastName = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_FAMILY_NAME").toString()

        Log.i("podaci", "$userEmail + $firstName + $lastName")

        Toast.makeText(context, "Uspješna Google prijava!", Toast.LENGTH_SHORT).show()

    } catch (e: GetCredentialException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Greška pri dobavljanju vjerodavnice", e)
        exception = e

    } catch (e: GoogleIdTokenParsingException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "$failureMessage: Greška parsiranja GoogleIdTokena", e)
        exception = e

    } catch (e: NoCredentialException) {
        Toast.makeText(context, "Nije pronađen Google račun na uređaju", Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Nije pronađen Google račun na uređaju", e)
        exception = e

    } catch (e: GetCredentialCustomException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Greška sa specijalnim zahtjevom vjerodavnica", e)
        exception = e

    } catch (e: GetCredentialCancellationException) {
        Toast.makeText(context, "Otkazana prijava Google-om", Toast.LENGTH_SHORT).show()
        Log.e(TAG, failureMessage + ": Otkazana prijava", e)
        exception = e
    }
    return exception
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun callPreview() {
    LoginScreen(onSuccessfulLogin = { _, _ -> })
}
