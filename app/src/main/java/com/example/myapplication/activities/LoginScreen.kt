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
    import com.example.myapplication.R
    import com.example.myapplication.viewmodels.LoginViewModel
    import com.google.android.libraries.identity.googleid.GetGoogleIdOption
    import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
    import kotlinx.coroutines.delay
    import java.security.SecureRandom
    import java.util.Base64


    var userEmail : String = ""
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun LoginScreen(
        viewModel: LoginViewModel = viewModel(),
        onSuccessfulLogin: (userId: Int, username: String) -> Unit ,
        //onGoogleLoginClick: () -> Unit
    ) {
        // Pratimo stanje iz ViewModel-a (LiveData paracetamol u Compose state)
        val username by viewModel.username.observeAsState("")
        val password by viewModel.password.observeAsState("")
        val errorMsg by viewModel.errorMessage.observeAsState("")
        val isLoading by viewModel.isLoading.observeAsState(false)

        val status by viewModel.status.observeAsState()
        val id by viewModel.userId.observeAsState()
        val webClientId = "66031714200-bcubnq7smv4mjhgl8jsg3p2c9k3t6hr1.apps.googleusercontent.com"
        var google by remember { mutableStateOf(false) }

        val onLoginSuccess by rememberUpdatedState(onSuccessfulLogin)

        LaunchedEffect(status) {
            try {
                if (status == "200" ) {
                    onLoginSuccess(id ?: -1, username)
                } else if (status == "404") {
                    Log.d("ne postoji", "taj korisnik")
                }
            } catch (t: Throwable) {
                Log.e("LoginScreen", "onSuccessfulLogin callback failed", t)
            }
        }

        if (google) {
            BottomSheet(webClientId = webClientId, viewModel) { success ->
                if(success) {
                    onLoginSuccess(1, "nesto")
                }
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
                    value = username ?: "",
                    onValueChange = { viewModel.username.value = it },
                    label = { Text("Korisničko ime") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Input polje za lozinku
                OutlinedTextField(
                    value = password ?: "",
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

                //Prijava putem Google-a - Franjo
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        google = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text("Google prijava")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }


    ////DODANE FUNKCIJE ZA IMPLEMENTACIJU GOOGLE PRIJAVE,
    ////KOD SA: https://codelabs.developers.google.com/sign-in-with-google-android#5

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun BottomSheet(webClientId: String, viewModel: LoginViewModel, onComplete: (Boolean) -> Unit = {}){
        val context = LocalContext.current
        var exception : Exception? = null

        LaunchedEffect(Unit) {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(webClientId)
                .setNonce(generateSecureRandomNonce())
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val e = signIn(request, context)

            Log.d("Usermail", userEmail.toString())
            if(userEmail.isNotEmpty()){
                val request = LoginRequest(username = userEmail.substringBefore("@"), "")
                Log.d("Ovo", request.username)
                viewModel.loginUserWithGoogle(request) { success ->
                    onComplete(success)
                }
                onComplete(true)
            }
            else{
                onComplete(false)
            }

            if (e is NoCredentialException) {
                val googleIdOptionFalse: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setNonce(generateSecureRandomNonce())
                    .build()

                val requestFalse: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOptionFalse)
                    .build()
               exception = signIn(requestFalse, context)

                //NESTO OVDJE NIJE U REDU - ne logira uopce
                Log.d("usermail", userEmail.toString())
               if(userEmail.isNotEmpty()){
                   val request = LoginRequest(username = userEmail.substringBefore("@"),"")
                   Log.d("ovo", request.username)
                   viewModel.loginUserWithGoogle(request) { success ->
                       onComplete(success)
                   }
                   onComplete(true)
               }
                else{
                    onComplete(false)
               }
            }
        }
        //if(exception != null) return false
        //return true
    }

    fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom.getInstanceStrong().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signIn(request: GetCredentialRequest, context: Context): Exception? {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Neuspješna prijava!"
        val e: Exception? = null

        delay(250)
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            userEmail = result.credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID").toString()

            //salje zahtjev prema serveru sa tim mailom / username-om
            //server provjeri jel postoji ako nije vraca 404,
            //ako server vrati 404 kazemo da je google sign in uspjesan ali korisnik ne postoji u bazi apk
            //jel zelimo dodat tog korisnika - on kaze da - dodavanje
            //on kaze ne - kazemo neuspjesna prijava i trpaj se


            // LOGIKA ZA DODAVANJE KORISNIKA KAO NOVOG AKO NE POSTOJI U BAZI....

            Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "Uspješna prijava!  ☜(ﾟヮﾟ☜)")

        } catch (e: GetCredentialException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, failureMessage + ": Greška pri dobavljanju vjerodavnice", e)

        } catch (e: GoogleIdTokenParsingException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$failureMessage: Greška parsiranja GoogleIdTokena", e)

        } catch (e: NoCredentialException) {
            Toast.makeText(context, "Nije pronađen Google račun na uređaju", Toast.LENGTH_SHORT).show()
            Log.e(TAG, failureMessage + ": Nije pronađen Google račun na uređaju", e)
            return e

        } catch (e: GetCredentialCustomException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, failureMessage + ": Greška sa specijalnim zahtjevom vjerodavnica", e)

        } catch (e: GetCredentialCancellationException) {
            Toast.makeText(context, "Otkazana prijava Google-om", Toast.LENGTH_SHORT).show()
            Log.e(TAG, failureMessage + ": Otkazana prijava", e)
        }
        return e
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Preview
    @Composable
    fun callPreview(){
        LoginScreen(onSuccessfulLogin = { userId, username -> })
    }