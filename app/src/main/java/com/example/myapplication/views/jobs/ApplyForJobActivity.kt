package com.example.myapplication.views.jobs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.JobApplication
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme
import java.time.LocalDate
import android.app.Activity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.views.getLoggedUserId
import kotlin.getValue

class ApplyForJobActivity : ComponentActivity() {

    private val applicationsViewModel: JobApplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        applicationsViewModel.uploadState.observe(this) { state ->
            if (state == "Uspješno dodana prijava") {
                Toast.makeText(this, state, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else if (!state.isNullOrEmpty()) {
                Toast.makeText(this, state, Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JobApplicationForm(
                        modifier = Modifier.padding(innerPadding),
                        applicationsViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun JobApplicationForm(modifier: Modifier, applicationsViewModel: JobApplicationViewModel) {
    var expectedPayText by remember { mutableStateOf<String>(value = "") }
    var workExperience by remember { mutableStateOf<String>(value = "") }
    var education by remember { mutableStateOf<String>(value = "") }
    var status by remember { mutableStateOf<String>(value = "") }


    val userId = getLoggedUserId()

    val context = LocalContext.current
    val jobId = remember {
        val prefs = context.getSharedPreferences("job_prefs", 0)
        prefs.getInt("jobId", -1)
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
                            val application = JobApplication(
                                id = null,
                                studentId = userId,
                                jobId = jobId,
                                employerId = 3,
                                dateOfSubmission = LocalDate.now().toString(),
                                status = status,
                                expectedPay = expectedPayInt,
                                workExperience = workExperience,
                                education = education
                            )

                            applicationsViewModel.uploadApplication(application)

                            if (applicationsViewModel.uploadState.value == "Uspješno dodana prijava") {
                                expectedPayText = ""
                                workExperience = ""
                                education = ""
                                status = ""
                            }
                            Log.d(
                                "Debugiranje!",
                                applicationsViewModel.uploadState.value.toString()
                            )

                            //Thread.sleep(3000)
                            //delay(3000)
                            //(context as? Activity)?.finish()
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Greška pri dodavanju oglasa - ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Debugiranje!", e.message.toString())
                        }
                    },

                    ) {
                    Text("Predaj prijavu na posao")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JobApplicationForm() {
    MyApplicationTheme {
        EditApplicationForm(modifier = Modifier)
    }
}