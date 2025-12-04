package com.example.myapplication.activities

import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.entities.JobListing
import com.example.myapplication.network.NetworkModule
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

//ova aktivnost omogućuje pregled oglasa studentima, uz dohvat i prikaz iz baze
class JobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JobListNetworkScreen(
                        modifier = Modifier
                            .padding(innerPadding)

                    )
                }
            }
        }
    }
}

@Composable
fun JobListNetworkScreen(modifier: Modifier = Modifier) {
    var jobs by remember { mutableStateOf<List<JobListing>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            val result = withContext(Dispatchers.IO) { NetworkModule.apiService.getJobs() }
            jobs = result
        } catch (e: Exception) {
            error = e.message ?: "Greška pri dohvaćanju podataka"
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
                        val termsJoined = job.terms.joinToString(" ")
                        val listingExpiresStr = try { job.listingExpires.toString() } catch (_: Exception) { "" }
                        listOf(
                            job.name,
                            job.description,
                            job.category,
                            job.location,
                            termsJoined,
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)

    ) {
        items(jobs) { job ->
            JobCard(job)
        }
    }
}

@Composable
fun JobCard(job: JobListing) {
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

            Text(text = "Terms: ${job.terms.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.size(6.dp))

            Text(text = "Listing expires: ${formatLocalDateTime(job.listingExpires)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatLocalDateTime(ldt: LocalDateTime): String {
    return try {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ldt.format(formatter)
    } catch (_: Exception) {
        ldt.toString()
    }
}
