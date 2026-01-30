package com.example.myapplication.activities

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodels.RegistrationViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.myapplication.R
import androidx.compose.ui.graphics.Color

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel(),
    onSuccessfulRegistration: (userId: Int, newUsername: String) -> Unit
) {
    val firstName by viewModel.firstName.observeAsState("")
    val lastName by viewModel.lastName.observeAsState("")
    val username by viewModel.username.observeAsState("")
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val confirm by viewModel.confirmPassword.observeAsState("")
    val role by viewModel.role.observeAsState("")
    val errorMsg by viewModel.errorMessage.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)), // ista pozadina kao UploadResume
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
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

        // Forma
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Registracija korisnika",
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
                value = firstName,
                onValueChange = { viewModel.firstName.value = it },
                label = { Text("Ime") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { viewModel.lastName.value = it },
                label = { Text("Prezime") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Korisničko ime") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Lozinka") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirm,
                onValueChange = { viewModel.confirmPassword.value = it },
                label = { Text("Ponovi lozinku") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Odaberi ulogu:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            viewModel.possibleRoles.forEach { roleName ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (role == roleName),
                        onClick = { viewModel.role.value = roleName }
                    )
                    Text(roleName, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.registerUser(
                        onSuccess = { id, newUser ->
                            onSuccessfulRegistration(id, newUser)
                        },
                        onFail = { }
                    )
                },
                enabled = !isLoading && firstName.isNotBlank() && lastName.isNotBlank() &&
                        username.isNotBlank() && email.isNotBlank() &&
                        password.isNotBlank() && confirm.isNotBlank()
            ) {
                Text(if (isLoading) "Dohvaćam..." else "Registracija")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
