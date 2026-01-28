package com.example.myapplication.views

import android.content.ContentProvider
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.views.jobs.CreateJobActivity
import com.example.myapplication.views.jobs.JobActivity
import com.example.myapplication.views.jobs.MyJobApplicationsActivity
import com.example.myapplication.views.jobs.ViewJobApplicationsForEmployerActivity
import com.example.myapplication.views.resume.UploadResumeActivity
import com.example.myapplication.activities.HomeActivity
import com.example.myapplication.views.jobs.ViewJobsForEmployer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", null) ?: "Nepoznati korisnik"

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        username = username,
                        onResumeClick = { startActivity(Intent(this, UploadResumeActivity::class.java)) },
                        onViewJobsClick = { startActivity(Intent(this, JobActivity::class.java)) },
                        onCreateJobsClick = { startActivity(Intent(this, CreateJobActivity::class.java)) },
                        onViewMyJobApplications = { startActivity(Intent(this, MyJobApplicationsActivity::class.java)) },
                        onViewJobsForEmployer = { startActivity(Intent(this, ViewJobsForEmployer::class.java)) },
                        onLogout = {
                            prefs.edit().clear().apply()
                            startActivity(
                                Intent(this, HomeActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                }
                            )
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    username: String,
    onResumeClick: () -> Unit = {},
    onViewJobsClick: () -> Unit = {},
    onCreateJobsClick: () -> Unit = {},
    onViewMyJobApplications: () -> Unit = {},
    onLogout: () -> Unit = {},
    onViewJobsForEmployer: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Gornji dio
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().weight(1f, fill = true)
        ) {
            HeaderUI()

            // username gore desno
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Korisnik: $username",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 8.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Odaberi opciju", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onResumeClick, modifier = Modifier.width(220.dp)) {
                        Text(text = "Životopis")
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = onViewJobsClick, modifier = Modifier.width(220.dp)) {
                        Text(text = "Pregled poslova")
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = onCreateJobsClick, modifier = Modifier.width(220.dp)) {
                        Text(text = "Kreiranje poslova")
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = onViewMyJobApplications, modifier = Modifier.width(220.dp)) {
                        Text(text = "Moje prijave na poslove (student)")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onViewJobsForEmployer, modifier = Modifier.width(220.dp)) {
                        Text(text = "Moji postavljeni poslovi (employer)")
                    }
                }
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(220.dp)
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Logout")
        }
    }
}
