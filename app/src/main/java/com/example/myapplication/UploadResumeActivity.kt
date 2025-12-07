package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

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

    // Initialize ViewModel with SharedPreferences on first composition
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("resume_prefs", 0)
        uploadViewModel.initialize(prefs)
    }

    // Observe state directly from the ViewModel
    val fileGuid by uploadViewModel.fileGuid.collectAsState()
    val fileName by uploadViewModel.fileName.collectAsState()
    val uploadState by uploadViewModel.uploadState.collectAsState()

    // Debug: Log every time state changes
    LaunchedEffect(fileGuid, fileName, uploadState) {
        Log.d("ResumeUploadScreen", "=== STATE CHANGED ===")
        Log.d("ResumeUploadScreen", "fileGuid: $fileGuid")
        Log.d("ResumeUploadScreen", "fileName: $fileName")
        Log.d("ResumeUploadScreen", "uploadState: $uploadState")
        Log.d("ResumeUploadScreen", "hasFile: ${fileGuid != null}")
    }

    // Local state for confirmation dialogs
    var showEditConfirmation by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // File picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { newUri: Uri? ->
        newUri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                Log.e("ResumeUploadScreen", "Failed to take persistable URI permission", e)
            }
            Log.d("ResumeUploadScreen", "File selected, starting upload")
            uploadViewModel.uploadOrUpdateResume(context, it)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
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
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "AI Career Buddy",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxHeight()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title and description
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                "Učitaj svoj životopis",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Učitaj svoj životopis za prijavu na oglas za posao ili za AI analizu sadržaja",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // File display box
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(Color(0xFFF5F7FB), shape = RoundedCornerShape(12.dp))
                .drawBehind {
                    drawRoundRect(
                        color = Color.Gray,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 0f)
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Key point: Use the fileGuid directly to determine what to show
            when {
                fileGuid.isNullOrBlank() -> {
                    Log.d("ResumeUploadScreen", "Rendering: Upload icon (no file)")
                    Text("📤", fontSize = 90.sp)
                }
                else -> {
                    Log.d("ResumeUploadScreen", "Rendering: File name = $fileName")
                    Text(
                        text = fileName ?: "CV.pdf",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status message area
        Box(
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uploadState) {
                is UploadState.Idle -> { /* No message */ }
                is UploadState.Uploading -> {
                    Text("Učitavanje...", color = Color.Gray)
                }
                is UploadState.Success -> {
                    Text(
                        state.message,
                        color = if (state.message.contains("deleted")) Color.Red else Color.Green,
                        textAlign = TextAlign.Center
                    )
                }
                is UploadState.Error -> {
                    Text(
                        state.message,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons - Key point: Use fileGuid directly
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            when {
                fileGuid.isNullOrBlank() -> {
                    Log.d("ResumeUploadScreen", "Rendering: Upload button")
                    Button(onClick = {
                        Log.d("ResumeUploadScreen", "Upload button clicked")
                        launcher.launch(arrayOf("application/pdf"))
                    }) {
                        Text("Prenesi životopis")
                    }
                }
                else -> {
                    Log.d("ResumeUploadScreen", "Rendering: Edit and Delete buttons")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = {
                            Log.d("ResumeUploadScreen", "Edit button clicked")
                            showEditConfirmation = true
                        }) {
                            Text("Uredi životopis")
                        }
                        Button(onClick = {
                            Log.d("ResumeUploadScreen", "Delete button clicked")
                            showDeleteConfirmation = true
                        }) {
                            Text("Obriši životopis")
                        }
                    }
                }
            }
        }
    }

    // Edit confirmation dialog
    if (showEditConfirmation) {
        AlertDialog(
            onDismissRequest = { showEditConfirmation = false },
            title = { Text("Potvrda") },
            text = { Text("Jeste li sigurni da želite zamjeniti dokument?") },
            confirmButton = {
                Button(onClick = {
                    showEditConfirmation = false
                    launcher.launch(arrayOf("application/pdf"))
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = { showEditConfirmation = false }) {
                    Text("Ne")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Potvrda") },
            text = { Text("Jeste li sigurni da želite obrisati životopis?") },
            confirmButton = {
                Button(onClick = {
                    showDeleteConfirmation = false
                    uploadViewModel.deleteResume()
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) {
                    Text("Ne")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResumeUploadPreview() {
    MyApplicationTheme {
        ResumeUploadScreen()
    }
}