package com.example.myapplication.views.jobs

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.models.JobListing
import com.example.myapplication.network.NetworkModule
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JobActivity : ComponentActivity() {
    private var refreshTrigger by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JobListNetworkScreen(
                        modifier = Modifier.padding(innerPadding),
                        refreshTrigger = refreshTrigger
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Increment to trigger refresh when returning from EditJobActivity
        refreshTrigger++
    }
}

@Composable
fun JobListNetworkScreen(modifier: Modifier = Modifier, refreshTrigger: Int = 0) {
    var jobs by remember { mutableStateOf<List<JobListing>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(refreshTrigger) {
        loading = true
        error = null
        try {
            val result = withContext(Dispatchers.IO) {
                NetworkModule.apiService.getJobs()
            }
            jobs = result
        } catch (e: Exception) {
            error = e.message ?: "Greška pri dohvaćanju podataka"
            Log.d("Debug", e.message.toString())
            jobs = null
        } finally {
            loading = false
        }
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(text = "Pretraži oglase") },
            placeholder = { Text(text = "Upiši tekst za pretraživanje po svim poljima") },
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            loading -> Text(text = "Učitavanje...", modifier = Modifier.padding(12.dp))
            !error.isNullOrEmpty() -> Text(text = "Greška: $error", modifier = Modifier.padding(12.dp))
            else -> {
                val filtered = remember(jobs, query) {
                    val q = query.trim().lowercase()
                    if (q.isEmpty()) jobs ?: emptyList()
                    else (jobs ?: emptyList()).filter { job ->
                        val listingExpiresStr = try { job.listingExpires.toString() } catch (_: Exception) { "" }
                        listOf(
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
                JobListScreen(jobs = filtered)
            }
        }
    }
}

@Composable
fun JobListScreen(jobs: List<JobListing>) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = 12.dp,
            bottom = 80.dp  // Extra padding at bottom
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        items(jobs) { job ->
            JobCard(
                job = job,
                onEditClick = {
                    // Navigate to EditJobActivity
                    val intent = Intent(context, EditJobActivity::class.java).apply {
                        putExtra("JOB_ID", job.id)
                        putExtra("JOB_NAME", job.name)
                        putExtra("JOB_DESCRIPTION", job.description)
                        putExtra("JOB_CATEGORY", job.category)
                        putExtra("JOB_LOCATION", job.location)
                        putExtra("JOB_LISTING_EXPIRES", job.listingExpires.toString())
                        putExtra("JOB_TERMS", job.terms)
                        putExtra("JOB_PAY_PER_HOUR", job.payPerHour)
                        putExtra("JOB_EMPLOYER_ID", job.employerId)
                    }
                    Log.d("ViewJobsActivity", "Editing job with ID: ${job.id}")
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun JobCard(job: JobListing, onEditClick: () -> Unit = {}) {
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

            Text(text = "Terms: ${job.terms}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Listing expires: ${formatLocalDateTime(job.listingExpires)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.size(12.dp))

            // NEW: Edit button
            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Uredi")
            }
        }
    }
}

private fun formatLocalDateTime(ldt: LocalDate?): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        ldt?.format(formatter) ?: LocalDate.now().toString()
    } catch (_: Exception) {
        ldt.toString()
    }
}