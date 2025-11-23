package com.example.myapplication.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.entities.JobListing
import com.example.myapplication.network.NetworkModule
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class JobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JobListNetworkScreen(modifier = Modifier.fillMaxSize())
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

    when {
        loading -> Text(text = "Učitavanje...", modifier = modifier.padding(12.dp))
        !error.isNullOrEmpty() -> Text(text = "Greška: $error", modifier = modifier.padding(12.dp))
        jobs == null || jobs?.isEmpty() == true -> Text(text = "Nema poslova za prikaz", modifier = modifier.padding(12.dp))
        else -> JobListScreen(jobs = jobs!!, modifier = modifier)
    }
}

@Composable
fun JobListScreen(jobs: List<JobListing>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(jobs) { job ->
            JobCard(job)
        }
    }
}

@Composable
fun JobCard(job: JobListing) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = job.category, style = MaterialTheme.typography.labelLarge)
                Text(text = "•", style = MaterialTheme.typography.labelLarge)
                Text(text = job.location, style = MaterialTheme.typography.labelLarge)
            }

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

@Preview(showBackground = true)
@Composable
fun JobListPreview() {
    MyApplicationTheme {
        JobListScreen(jobs = emptyList(), modifier = Modifier.fillMaxSize())
    }
}