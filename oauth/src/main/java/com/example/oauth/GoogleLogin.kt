package com.example.oauth

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.delay
import java.security.SecureRandom
import java.util.Base64

data class GoogleUserProfile(
    val email: String,
    val firstName: String,
    val lastName: String
)

data class GoogleLoginResult(
    val profile: GoogleUserProfile?,
    val exception: Exception?
)

object GoogleLogin {
    fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom.getInstanceStrong().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }
    private fun buildRequest(
        webClientId: String,
        filterByAuthorizedAccounts: Boolean
    ): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(webClientId)
            .setNonce(generateSecureRandomNonce())
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signIn(
        context: Context,
        webClientId: String,
        filterByAuthorizedAccounts: Boolean = true
    ): GoogleLoginResult {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Neuspješna prijava!"
        var exception: Exception? = null

        delay(250)
        return try {
            val request = buildRequest(webClientId, filterByAuthorizedAccounts)
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val data = result.credential.data
            val email = data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID").orEmpty()
            val firstName = data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_GIVEN_NAME").orEmpty()
            val lastName = data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_FAMILY_NAME").orEmpty()

            Log.i("GoogleLogin", "$email + $firstName + $lastName")
            Toast.makeText(context, "Uspješna Google prijava!", Toast.LENGTH_SHORT).show()
            GoogleLoginResult(GoogleUserProfile(email, firstName, lastName), null)

        } catch (e: GetCredentialException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e("GoogleLogin", "$failureMessage: Greška pri dobavljanju vjerodavnice", e)
            exception = e
            GoogleLoginResult(null, exception)

        } catch (e: GoogleIdTokenParsingException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e("GoogleLogin", "$failureMessage: Greška parsiranja GoogleIdTokena", e)
            exception = e
            GoogleLoginResult(null, exception)

        } catch (e: NoCredentialException) {
            Toast.makeText(context, "Nije pronađen Google račun na uređaju", Toast.LENGTH_SHORT).show()
            Log.e("GoogleLogin", "$failureMessage: Nije pronađen Google račun na uređaju", e)
            exception = e
            GoogleLoginResult(null, exception)

        } catch (e: GetCredentialCustomException) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            Log.e("GoogleLogin", "$failureMessage: Greška sa specijalnim zahtjevom vjerodavnica", e)
            exception = e
            GoogleLoginResult(null, exception)

        } catch (e: GetCredentialCancellationException) {
            Toast.makeText(context, "Otkazana prijava Google-om", Toast.LENGTH_SHORT).show()
            Log.e("GoogleLogin", "$failureMessage: Otkazana prijava", e)
            exception = e
            GoogleLoginResult(null, exception)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInWithFallback(
        context: Context,
        webClientId: String
    ): GoogleLoginResult {
        val primary = signIn(context, webClientId, filterByAuthorizedAccounts = true)
        if (primary.profile != null || primary.exception !is NoCredentialException) {
            return primary
        }

        val secondary = signIn(context, webClientId, filterByAuthorizedAccounts = false)
        return if (secondary.profile != null) secondary else GoogleLoginResult(null, secondary.exception ?: primary.exception)
    }
}
