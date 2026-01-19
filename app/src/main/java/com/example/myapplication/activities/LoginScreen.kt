// app/src/main/java/com/example/myapplication/activities/LoginScreen.kt
package com.example.myapplication.activities

import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.LoginRequest
import com.example.myapplication.viewmodels.LoginViewModel
import com.example.myapplication.R
import com.example.myapplication.views.getLoggedUserId

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onSuccessfulLogin: (userId: Int, username: String) -> Unit
) {
    // Pratimo stanje iz ViewModel-a (LiveData paracetamol u Compose state)
    val username by viewModel.username.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val errorMsg by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)

    val status by viewModel.status.observeAsState()
    val id by viewModel.userId.observeAsState()

    LaunchedEffect(status) {
        if(status == "200") {
            onSuccessfulLogin(id?.toInt() ?: -1,username)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),  // svijetloplava pozadina (kao na RegistrationScreen)
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Zaglavlje s naslovom aplikacije i logom (isto kao u RegistrationScreen)
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

        // Form za prijavu
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

            // Prikaz poruke o grešci ako postoji
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Input polje za korisničko ime
            val fieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Korisničko ime") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Input polje za lozinku
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Lozinka") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gumb za prijavu
            Button(
                onClick = {
                    viewModel.loginUser(
                        onSuccess = { id, user ->
                            onSuccessfulLogin(id, user)
                        },
                        onFail = { /* ništa - errorMsg će biti prikazan */ }
                    )
                },
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isLoading) "Dohvaćam..." else "Prijava")
            }

            //Dodan gumb za prijavu putem Google-a - Franjo
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    val request = LoginRequest(username.toString(), password.toString())
                    viewModel.loginUserWithGoogle(request)
                },
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Google prijava")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Preview
@Composable
fun callPreview(){
    LoginScreen(onSuccessfulLogin = { userId, username -> })
}