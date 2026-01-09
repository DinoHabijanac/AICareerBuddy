package com.example.myapplication.views.jobs

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.JobApplication
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.getLoggedUserId
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.time.LocalDate

class EditApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EditApplicationForm(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun EditApplicationForm(modifier : Modifier, applicationsViewModel: JobApplicationViewModel = viewModel()) {

    val context = LocalContext.current
    val applicationId = remember {
        val prefs = context.getSharedPreferences("application_prefs", 0)
        prefs.getInt("applicationId", -1)
    }

    val application by applicationsViewModel.getApplicationsById(applicationId).observeAsState()
    val uploadState by applicationsViewModel.uploadState.observeAsState()
    val uploadCode by applicationsViewModel.uploadCode.observeAsState()

    var expectedPayText by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var workExperience by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var education by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }
    var status by rememberSaveable { androidx.compose.runtime.mutableStateOf("") }

    val userId = getLoggedUserId()

    LaunchedEffect(application) {
        application?.let { app ->
            expectedPayText = app.expectedPay?.toString() ?: ""
            workExperience = app.workExperience
            education = app.education
            status = app.status
        }
    }

    LaunchedEffect(uploadState) {
        if (uploadState == "Uspješno promjenjena prijava") {
            expectedPayText = ""
            workExperience = ""
            education = ""
            status = ""
            Toast.makeText(context, "Uspješno promjenjena prijava", Toast.LENGTH_SHORT).show()
            delay(3000)
            (context as? Activity)?.finish()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = expectedPayText,
                onValueChange = { new ->
                    expectedPayText = new.filter { it.isDigit() }
                },
                label = { Text("Očekivana plaća po satu")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = workExperience,
                onValueChange = { workExperience = it },
                label = { Text("Radno iskustvo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OutlinedTextField(
                value = education,
                onValueChange = { education = it },
                label = { Text("Edukacija") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            OutlinedTextField(
                value = status,
                onValueChange = { status = it },
                label = { Text("Status") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Row(
                modifier = Modifier.padding(5.dp, 10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                )
            {
                Button(
                    onClick = {
                        try {
                            val expectedPayInt =
                                expectedPayText.takeIf { it.isNotBlank() }?.toIntOrNull()
                            val applicationToEdit = JobApplication(
                                id = application?.id,
                                studentId = userId,
                                jobId = application?.jobId ?: 33,
                                employerId =  3, // TODO(ISPRAVITI)
                                dateOfSubmission = LocalDate.now().toString(),
                                status = status,
                                expectedPay = expectedPayInt,
                                workExperience = workExperience,
                                education = education,
                                interviewDate = null
                            )
                            applicationsViewModel.editApplication(applicationToEdit)

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Greška pri dodavanju oglasa - ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Debugiranje!", e.message.toString())                        }
                    },
                    ) {
                    Text("Predaj izmjenu prijave za posao")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditApplicationFormPreview2() {
    MyApplicationTheme {
        EditApplicationForm(modifier = Modifier)
    }
}