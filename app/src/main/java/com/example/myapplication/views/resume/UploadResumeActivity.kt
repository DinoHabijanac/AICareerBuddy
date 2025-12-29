package com.example.myapplication.views.resume

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodels.DeleteState
import com.example.myapplication.viewmodels.UploadState
import com.example.myapplication.viewmodels.UploadViewModel
import com.example.myapplication.views.getLoggedUserId

class UploadResumeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResumeUploadScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ResumeUploadScreen(
    modifier: Modifier = Modifier,
    uploadViewModel: UploadViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUri = remember { mutableStateOf<Uri?>(null) }
    val uploadState by uploadViewModel.uploadState.collectAsState()
    val deleteState by uploadViewModel.deleteState.collectAsState()

    // Get the logged-in user's ID from SharedPreferences
    val userId = getLoggedUserId()

    // Redirect to login if not logged in
    LaunchedEffect(userId) {
        if (userId == -1) {
            Toast.makeText(
                context,
                "Morate biti prijavljeni za pristup ovoj stranici",
                Toast.LENGTH_LONG
            ).show()
            // Optional: Navigate back to HomeActivity/LoginActivity
            // (context as? ComponentActivity)?.finish()
        }
    }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("resume_prefs", 0)
        val uriString = prefs.getString("resume_uri", null)
        uriString?.let {
            try {
                currentUri.value = it.toUri()
            } catch (_: Exception) {
            }
        }
    }

    // Handle delete state
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteState.Success -> {
                Toast.makeText(
                    context,
                    (deleteState as DeleteState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
                // Clear local storage
                val prefs = context.getSharedPreferences("resume_prefs", 0)
                prefs.edit { remove("resume_uri") }
                currentUri.value = null
            }
            is DeleteState.Error -> {
                Toast.makeText(
                    context,
                    (deleteState as DeleteState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                }

                val prefs = context.getSharedPreferences("resume_prefs", 0)
                prefs.edit { putString("resume_uri", it.toString()) }
                currentUri.value = it

                // Use the real logged-in user's ID
                if (userId != -1) {
                    // Ako već postoji resume, koristi UPDATE, inače POST
                    val existingResume = currentUri.value
                    if (existingResume != null && existingResume != it) {
                        uploadViewModel.updateResume(context, it, userId)
                    } else {
                        uploadViewModel.uploadResume(context, it, userId)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Greška: Niste prijavljeni",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Učitaj svoj životopis", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Učitaj svoj životopis za prijavu na oglas za posao ili za AI analizu sadržaja",
                textAlign = TextAlign.Center
            )
        }

        val boxSize = 220.dp
        Box(
            modifier = Modifier
                .size(boxSize)
                .drawBehind {
                    val strokePx = with(density) { 2.dp.toPx() }
                    drawRoundRect(
                        color = Color.Transparent,
                        cornerRadius = CornerRadius(
                            12.dp.toPx(),
                            12.dp.toPx()
                        ),
                        style = Stroke(
                            width = strokePx,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                        )
                    )
                }
                .background(Color(0xFFF5F7FB), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val uri = currentUri.value
            if (uri == null) {
                Text("📤", fontSize = 90.sp)
            } else {
                val name = remember(uri) {
                    var display = uri.lastPathSegment ?: uri.toString()
                    try {
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                if (idx >= 0) display = it.getString(idx)
                            }
                        }
                    } catch (_: Exception) {
                    }
                    display
                }
                Text(
                    text = name,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (uploadState) {
                is UploadState.Idle -> {}
                is UploadState.Uploading -> Text("Učitavanje...", color = Color.Gray)
                is UploadState.Success -> Text(
                    (uploadState as UploadState.Success).message,
                    color = Color.Green
                )
                is UploadState.Error -> Text(
                    (uploadState as UploadState.Error).message,
                    color = Color.Red
                )
            }

            when (deleteState) {
                is DeleteState.Deleting -> Text("Brisanje...", color = Color.Gray)
                else -> {}
            }

            currentUri.value?.let { uri ->
                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setData(uri)
                            flags =
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Ne mogu otvoriti dokument: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }) {
                    Text("Otvori")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (userId != -1) {
                            uploadViewModel.deleteResume(userId)
                        } else {
                            Toast.makeText(
                                context,
                                "Greška: Niste prijavljeni",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    enabled = deleteState !is DeleteState.Deleting && userId != -1
                ) {
                    Text("Obriši sa servera")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        launcher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "*/*"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3) // Blue
                    ),
                    enabled = userId != -1
                ) {
                    Text("Zamijeni životopis")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val prefs = context.getSharedPreferences("resume_prefs", 0)
                    prefs.edit { remove("resume_uri") }
                    currentUri.value = null
                    uploadViewModel.reset()
                }) {
                    Text("Ukloni lokalno")
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    launcher.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                            "*/*"
                        )
                    )
                },
                enabled = currentUri.value == null && userId != -1
            ) {
                Text("Prenesi životopis")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResumeUploadPreview() {
    MyApplicationTheme {
        ResumeUploadScreen(Modifier.background(Color(0xFFBBDEFB)))
    }
}