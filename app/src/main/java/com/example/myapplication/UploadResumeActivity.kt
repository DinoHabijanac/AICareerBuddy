package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
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
fun ResumeUploadScreen(modifier: Modifier = Modifier, uploadViewModel: UploadViewModel = viewModel()) {
    val context = LocalContext.current
    val (currentUri, setCurrentUri) = remember { mutableStateOf<Uri?>(null) }
    val uploadState by uploadViewModel.uploadState.collectAsState()
    val prefs = remember { context.getSharedPreferences("resume_prefs", 0) }

    val showEditConfirmation = remember { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    // This LaunchedEffect reacts to the result from the ViewModel
    LaunchedEffect(uploadState) {
        val state = uploadState
        if (state is UploadState.Success) {
            // Wait for 2 seconds so the user can read the message
            delay(2000)
            if (state.message.contains("obrisan", ignoreCase = true)) {
                // If deleted, clear the UI
                prefs.edit { remove("resume_uri") }
                setCurrentUri(null)
                uploadViewModel.reset()
            } else {
                // For other successes (upload/update), just reset the state
                uploadViewModel.reset()
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { newUri: Uri? ->
        newUri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) { }

            prefs.edit { putString("resume_uri", it.toString()) }
            setCurrentUri(it)

            // The update/upload logic is now separated from the result handling
            if (currentUri != null) {
                uploadViewModel.updateResume(context, it)
            } else {
                uploadViewModel.uploadResume(context, it)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().background(Color.Blue).height(80.dp)
        ) { Text(text = "AI Career Buddy", style = MaterialTheme.typography.headlineLarge); Spacer(modifier = Modifier.weight(1f)); Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.fillMaxHeight()) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Učitaj svoj životopis", style = MaterialTheme.typography.headlineSmall); Spacer(modifier = Modifier.height(8.dp)); Text("Učitaj svoj životopis za prijavu na oglas za posao ili za AI analizu sadržaja", textAlign = TextAlign.Center) }

        Box(
            modifier = Modifier.size(220.dp).background(Color(0xFFF5F7FB), shape = RoundedCornerShape(12.dp)).drawBehind { drawRoundRect(color = Color.Gray, cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()), style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f))) },
            contentAlignment = Alignment.Center
        ) {
            if (currentUri == null) {
                Text("📤", fontSize = 90.sp)
            } else {
                val fileName = remember(currentUri) { getFileName(context, currentUri) }
                Text(text = fileName, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(12.dp))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.height(48.dp)) {
            when (val state = uploadState) {
                is UploadState.Idle -> { /* Idle */ }
                is UploadState.Uploading -> Text("Učitavanje...", color = Color.Gray)
                is UploadState.Success -> {
                    val color = when {
                        state.message.contains("ažuriran", ignoreCase = true) -> Color.Blue
                        state.message.contains("obrisan", ignoreCase = true) -> Color.Red
                        else -> Color.Green
                    }
                    Text(state.message, color = color, textAlign = TextAlign.Center)
                }
                is UploadState.Error -> Text(state.message, color = Color.Red, textAlign = TextAlign.Center)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 32.dp)) {
            if (currentUri == null) {
                Button(onClick = { launcher.launch(arrayOf("application/pdf", "*/*")) }) {
                    Text("Prenesi životopis")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { showEditConfirmation.value = true }) {
                        Text("Uredi životopis")
                    }
                    Button(onClick = { showDeleteConfirmation.value = true }) {
                        Text("Obriši životopis")
                    }
                }
            }
        }
    }

    if (showEditConfirmation.value) {
        AlertDialog(
            onDismissRequest = { showEditConfirmation.value = false },
            title = { Text("Potvrda") },
            text = { Text("Jeste li sigurni da želite zamjeniti dokument?") },
            confirmButton = { Button(onClick = { launcher.launch(arrayOf("application/pdf", "*/*")); showEditConfirmation.value = false }) { Text("Da") } },
            dismissButton = { Button(onClick = { showEditConfirmation.value = false }) { Text("Ne") } }
        )
    }

    if (showDeleteConfirmation.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation.value = false },
            title = { Text("Potvrda") },
            text = { Text("Jeste li sigurni da želite obrisati životopis?") },
            confirmButton = { Button(onClick = { uploadViewModel.deleteResume(); showDeleteConfirmation.value = false }) { Text("Da") } },
            dismissButton = { Button(onClick = { showDeleteConfirmation.value = false }) { Text("Ne") } }
        )
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String {
    var displayName = "Nepoznata datoteka"
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) displayName = it.getString(nameIndex)
            }
        }
    } catch (e: Exception) { displayName = uri.lastPathSegment ?: displayName }
    return displayName
}

@Preview(showBackground = true)
@Composable
fun ResumeUploadPreview() {
    MyApplicationTheme { ResumeUploadScreen() }
}
