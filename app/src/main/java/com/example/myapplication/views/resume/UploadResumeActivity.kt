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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontStyle
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.ImprovementSection
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodels.DeleteState
import com.example.myapplication.viewmodels.UploadState
import com.example.myapplication.viewmodels.UploadViewModel
import com.example.myapplication.views.HeaderUI
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
    val aiFeedback by uploadViewModel.aiFeedback.collectAsState()
    val showAiDialog = remember { mutableStateOf(false) }
    val buttonWidth = 220.dp

    val improvements by uploadViewModel.improvements.collectAsState()
    val improvementsLoading by uploadViewModel.improvementsLoading.collectAsState()
    val improvementsError by uploadViewModel.improvementsError.collectAsState()
    val showImprovementsDialog = remember { mutableStateOf(false) }

    val userId = getLoggedUserId()

    LaunchedEffect(aiFeedback) {
        showAiDialog.value = aiFeedback != null
    }

    LaunchedEffect(improvements) {
        if (improvements != null) {
            showImprovementsDialog.value = true
        }
    }

    LaunchedEffect(userId) {
        if (userId == -1) {
            Toast.makeText(
                context,
                "Morate biti prijavljeni za pristup ovoj stranici",
                Toast.LENGTH_LONG
            ).show()
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

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is DeleteState.Success -> {
                Toast.makeText(
                    context,
                    (deleteState as DeleteState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
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

                if (userId != -1) {
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
            .statusBarsPadding()
            .padding(bottom = 32.dp),
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

        val boxSize = 180.dp
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

                Button(
                    modifier = Modifier.width(buttonWidth),
                    onClick = {
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
                    }
                ) {
                    Text("Otvori")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.width(buttonWidth),
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
                    modifier = Modifier.width(buttonWidth),
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
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = userId != -1
                ) {
                    Text("Zamijeni životopis")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.width(buttonWidth),
                    onClick = {
                        val prefs = context.getSharedPreferences("resume_prefs", 0)
                        prefs.edit { remove("resume_uri") }
                        currentUri.value = null
                        uploadViewModel.reset()
                    }
                ) {
                    Text("Ukloni lokalno")
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                modifier = Modifier.width(buttonWidth),
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

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.width(buttonWidth),
                onClick = {
                    uploadViewModel.analyzeResume(userId)
                },
                enabled = currentUri.value != null && userId != -1
            ) {
                Text("AI analiza životopisa")
            }
        }
    }

    // PRVI DIALOG - AI ANALIZA
    if (showAiDialog.value && aiFeedback != null) {
        Dialog(
            onDismissRequest = {
                showAiDialog.value = false
                uploadViewModel.clearAiFeedback()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI analiza životopisa",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = aiFeedback?.feedback.orEmpty(),
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            uploadViewModel.analyzeImprovements(userId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        enabled = !improvementsLoading
                    ) {
                        if (improvementsLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generira prijedloge...")
                        } else {
                            Text("Prijedlozi poboljšanja")
                        }
                    }

                    if (improvementsError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = improvementsError ?: "",
                            color = Color(0xFFD32F2F),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showAiDialog.value = false
                            uploadViewModel.clearAiFeedback()
                            uploadViewModel.clearImprovements()
                        }
                    ) {
                        Text("Zatvori")
                    }
                }
            }
        }
    }

    // DRUGI DIALOG - PRIJEDLOZI POBOLJŠANJA
    if (showImprovementsDialog.value && improvements != null) {
        Dialog(
            onDismissRequest = {
                showImprovementsDialog.value = false
                uploadViewModel.clearImprovements()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Prijedlozi poboljšanja",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFD32F2F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val summary = improvements?.overallSummary
                    if (!summary.isNullOrBlank()) {
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF616161),
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    val sections = improvements?.sections ?: emptyList()
                    sections.forEachIndexed { index, section ->
                        ImprovementSectionCard(section)
                        if (index < sections.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showImprovementsDialog.value = false
                            uploadViewModel.clearImprovements()
                        }
                    ) {
                        Text("Zatvori")
                    }
                }
            }
        }
    }
}

@Composable
fun ImprovementSectionCard(section: ImprovementSection) {
    val isOk = section.status.trim().equals("U redu", ignoreCase = true)

    val statusColor = if (isOk) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val bgColor     = if (isOk) Color(0xFFF1F8E9) else Color(0xFFFFF3E0)
    val borderColor = if (isOk) Color(0xFFA5D6A7) else Color(0xFFFFCC80)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .background(statusColor, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isOk) "✓ U redu" else "⚠ Poboljšiti",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = section.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF424242)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResumeUploadPreview() {
    MyApplicationTheme {
        ResumeUploadScreen(Modifier.background(Color(0xFFBBDEFB)))
    }
}