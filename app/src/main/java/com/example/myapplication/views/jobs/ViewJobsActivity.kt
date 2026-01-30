
package com.example.myapplication.views.jobs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.models.JobApplication
import com.example.core.models.JobListing
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.viewmodels.JobsViewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.getLoggedUserId
import com.example.myapplication.views.formatLocalDateTime

class JobActivity : ComponentActivity() {

    private val jobsViewModel: JobsViewModel by viewModels()
    private val applicationsViewModel: JobApplicationViewModel by viewModels()

    private val applyForJobLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                jobsViewModel.getJobs2()
                val userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", 0)
                applicationsViewModel.getApplicationsForStudent(userId)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JobListNetworkScreen(
                        modifier = Modifier.padding(innerPadding),
                        onApplyClick = { job ->
                            val prefs = getSharedPreferences("job_prefs", MODE_PRIVATE)
                            prefs.edit().putInt("jobId", job.id ?: 0).apply()

                            val intent = Intent(this, ApplyForJobActivity::class.java)
                            applyForJobLauncher.launch(intent)
                        },
                        jobsViewModel,
                        applicationsViewModel
                    )
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        jobsViewModel.getJobs2()
    }
}

@Composable
fun JobListNetworkScreen(modifier: Modifier = Modifier, onApplyClick : (JobListing) -> Unit = {}, jobsViewModel : JobsViewModel, applicationsViewModel : JobApplicationViewModel) {
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }
    val applied by applicationsViewModel.applications.observeAsState(emptyList())
    val jobs by jobsViewModel.jobs.observeAsState(emptyList())

    val userId = getLoggedUserId()

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            jobsViewModel.getJobs2()
            applicationsViewModel.getApplicationsForStudent(userId = userId)
            Log.d("logovi", applied.toString())
        } catch (e: Exception) {
            error = e.message ?: "Greška pri dohvaćanju podataka"
            Log.d("Debug", e.message.toString())
        } finally {
            loading = false
        }
    }
    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(text = "Pretraži oglase") },
            placeholder = { Text(text = "Upiši tekst za pretraživanje po svim poljima") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> Text(text = "Učitavanje...", modifier = Modifier.padding(12.dp))
            !error.isNullOrEmpty() -> Text(
                text = "Greška: $error",
                modifier = Modifier.padding(12.dp)
            )

            else -> {
                val filtered = remember(jobs, query) {
                    val q = query.trim().lowercase()
                    if (q.isEmpty()) jobs
                    else (jobs).filter { job ->
                        val listingExpiresStr = try {
                            job.listingExpires.toString()
                        } catch (_: Exception) {
                            ""
                        }
                        listOf(
                            job.id,
                            job.name,
                            job.description,
                            job.category,
                            job.location,
                            job.terms,
                            job.payPerHour.toString(),
                            listingExpiresStr
                        ).joinToString(" ").lowercase().contains(q)
                    }
                }
                JobListScreen(jobs = filtered, applied = applied, onApplyClick = onApplyClick)
            }
        }
    }
}

@Composable
fun JobListScreen(jobs: List<JobListing>, applied : List<JobApplication>?, onApplyClick: (JobListing) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        items(jobs) { job ->
            val application = applied?.find { application -> application.jobId == job.id }
            var appliedForJob = false
            Log.d("logoviii", application.toString())
            if(application != null){
               appliedForJob = true
            }
            JobCard(job, appliedForJob, onApplyClick)
        }
    }
}

@Composable
fun JobCard(job: JobListing, applied: Boolean, onApplyClick: (JobListing) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = job.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "€${job.payPerHour}/h", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(text = "Uvjeti: ${job.terms}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Oglas ističe: ${formatLocalDateTime(job.listingExpires)}",
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = { onApplyClick(job) },
                enabled = !applied
            ) {
                if(applied)
                    Text("Već poslana prijava")
                else
                    Text("Prijavi se na ovaj oglas", )
            }
        }
    }
}