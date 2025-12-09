package com.example.myapplication.views.jobs

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.models.JobListing
import com.example.myapplication.viewmodels.JobsViewModel
import com.example.myapplication.views.ui.theme.MyApplicationTheme
import java.time.LocalDate

class EditJobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get job data from intent
        val jobId = intent.getIntExtra("JOB_ID", 0)
        val jobName = intent.getStringExtra("JOB_NAME") ?: ""
        val jobDescription = intent.getStringExtra("JOB_DESCRIPTION") ?: ""
        val jobCategory = intent.getStringExtra("JOB_CATEGORY") ?: ""
        val jobLocation = intent.getStringExtra("JOB_LOCATION") ?: ""
        val jobListingExpires = intent.getStringExtra("JOB_LISTING_EXPIRES") ?: ""
        val jobTerms = intent.getStringExtra("JOB_TERMS") ?: ""
        val jobPayPerHour = intent.getIntExtra("JOB_PAY_PER_HOUR", 0)
        val jobEmployerId = intent.getIntExtra("JOB_EMPLOYER_ID", 0)

        Log.d("EditJobActivity", "Received job ID: $jobId")
        Log.d("EditJobActivity", "Received job name: $jobName")
        Log.d("EditJobActivity", "Received employer ID: $jobEmployerId")

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EditJobScreen(
                        modifier = Modifier.padding(innerPadding),
                        jobId = jobId,
                        initialName = jobName,
                        initialDescription = jobDescription,
                        initialCategory = jobCategory,
                        initialLocation = jobLocation,
                        initialListingExpires = jobListingExpires,
                        initialTerms = jobTerms,
                        initialPayPerHour = jobPayPerHour.toString(),
                        initialEmployerId = jobEmployerId,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun EditJobScreen(
    modifier: Modifier = Modifier,
    jobsViewModel: JobsViewModel = viewModel(),
    jobId: Int,
    initialName: String,
    initialDescription: String,
    initialCategory: String,
    initialLocation: String,
    initialListingExpires: String,
    initialTerms: String,
    initialPayPerHour: String,
    initialEmployerId: Int,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription) }
    var category by remember { mutableStateOf(initialCategory) }
    var location by remember { mutableStateOf(initialLocation) }
    var listingExpires by remember { mutableStateOf(initialListingExpires) }
    var terms by remember { mutableStateOf(initialTerms) }
    var payPerHour by remember { mutableStateOf(initialPayPerHour) }

    var showUnsavedDialog by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    val updateState by jobsViewModel.updateState.observeAsState()

    // Track changes
    LaunchedEffect(name, description, category, location, listingExpires, terms, payPerHour) {
        hasChanges = name != initialName ||
                description != initialDescription ||
                category != initialCategory ||
                location != initialLocation ||
                listingExpires != initialListingExpires ||
                terms != initialTerms ||
                payPerHour != initialPayPerHour
        jobsViewModel.markUnsavedChanges(hasChanges)
    }

    // Handle update state
    LaunchedEffect(updateState) {
        updateState?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (it.contains("uspješno", ignoreCase = true)) {
                hasChanges = false
                jobsViewModel.markUnsavedChanges(false)
                // Wait a moment then go back
                kotlinx.coroutines.delay(1000)
                onBackPressed()
            }
        }
    }

    // Unsaved changes dialog
    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text(text = "Nespremljene promjene") },
            text = { Text(text = "Imate nespremljene promjene. Jeste li sigurni da želite napustiti formu?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    onBackPressed()
                }) {
                    Text(text = "Da, napusti")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text(text = "Odustani")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Uredi oglas za posao",
                style = MaterialTheme.typography.headlineSmall,
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Naziv oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Opis oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(text = "Kategorija oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(text = "Lokacija oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            // Reusing DateOnlyPicker from CreateJobActivity
            DateOnlyPicker(
                label = "Datum isteka oglasa",
                initialIsoDate = listingExpires.ifBlank { null },
                onDateSelected = { iso -> listingExpires = iso }
            )

            OutlinedTextField(
                value = terms,
                onValueChange = { terms = it },
                label = { Text(text = "Uvjeti oglasa (komunikativnost, spremnost ...)") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            OutlinedTextField(
                value = payPerHour,
                onValueChange = { payPerHour = it },
                label = { Text(text = "Plaća po satu") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    onClick = {
                        if (hasChanges) {
                            showUnsavedDialog = true
                        } else {
                            onBackPressed()
                        }
                    }
                ) {
                    Text(text = "Odustani")
                }

                Button(
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    onClick = {
                        try {
                            if (name.isBlank() || description.isBlank() ||
                                category.isBlank() || location.isBlank() ||
                                listingExpires.isBlank() || terms.isBlank() ||
                                payPerHour.isBlank()) {
                                Toast.makeText(context, "Sva polja moraju biti popunjena", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (jobId == 0) {
                                Toast.makeText(context, "Greška: Nevažeći ID oglasa", Toast.LENGTH_SHORT).show()
                                Log.e("EditJobActivity", "Cannot update job with ID = 0")
                                return@Button
                            }

                            val job = JobListing(
                                id = jobId,
                                name = name,
                                description = description,
                                category = category,
                                location = location,
                                listingExpires = LocalDate.parse(listingExpires),
                                terms = terms,
                                payPerHour = payPerHour.toInt(),
                                employerId = initialEmployerId
                            )
                            Log.d("EditJobActivity", "Updating job: $job")
                            jobsViewModel.updateJob(job)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Greška pri ažuriranju oglasa - ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("EditJobActivity", "Update error", e)
                        }
                    }
                ) {
                    Text(text = "Spremi promjene")
                }
            }
        }
    }
}