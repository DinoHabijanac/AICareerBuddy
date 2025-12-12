package com.example.myapplication.views

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.helpers.HeaderUI
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.views.jobs.CreateJobActivity
import com.example.myapplication.views.jobs.JobActivity
import com.example.myapplication.views.jobs.MyJobApplicationsActivity
import com.example.myapplication.views.jobs.ViewJobApplicationsForEmployerActivity
import com.example.myapplication.views.resume.UploadResumeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onResumeClick = {
                            startActivity(Intent(this, UploadResumeActivity::class.java))
                        },
                        onViewJobsClick = {
                            startActivity(Intent(this, JobActivity::class.java))
                        },
                        onCreateJobsClick = {
                            startActivity(Intent(this, CreateJobActivity::class.java))
                        },
                        onViewMyJobApplications = {
                            startActivity(Intent(this, MyJobApplicationsActivity::class.java))
                        },
                        onViewJobApplicationsForEmployer = {
                            startActivity(Intent(this, ViewJobApplicationsForEmployerActivity::class.java))
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
    onResumeClick: () -> Unit = {},
    onViewJobsClick: () -> Unit = {},
    onCreateJobsClick: () -> Unit = {},
    onViewMyJobApplications: () -> Unit = {},
    onViewJobApplicationsForEmployer: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            Button(onClick = onViewJobApplicationsForEmployer, modifier = Modifier.width(220.dp)) {
                Text(text = "Moji postavljeni poslovi (employer)")
            }
        }
    }
}
