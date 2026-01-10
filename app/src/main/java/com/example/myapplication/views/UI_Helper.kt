package com.example.myapplication.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.JobApplication
import com.example.myapplication.R
import com.example.myapplication.viewmodels.JobsViewModel

@Composable
fun HeaderUI(modifier : Modifier = Modifier) {
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
    }

@Composable
fun ListApplications(
    applications: List<JobApplication>,
    onAction1Click: (JobApplication) -> Unit,
    onAction2Click: (JobApplication) -> Unit,
    name1: String,
    name2: String
) {
    val jobsViewModel : JobsViewModel = viewModel()
    val job by jobsViewModel.job.observeAsState()
    val student by jobsViewModel.student.observeAsState()

        LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        items(applications) { application ->
            jobsViewModel.getJobById(application.jobId)
            jobsViewModel.getStudentById(application.studentId)
            ApplicationCard(application,
                job?.name , student.toString(), onAction1Click = onAction1Click, onAction2Click = onAction2Click, name1, name2)
        }  
    }
}
@Composable
fun ApplicationCard(
    application: JobApplication,
    jobName: String?,
    studentName: String,
    onAction1Click: (JobApplication) -> Unit,
    onAction2Click: (JobApplication) -> Unit,
    name1: String,
    name2: String
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors())
    {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Status: ${application.status}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = application.expectedPay?.let { "€$it/h" } ?: "No pay expectation",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = application.workExperience,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Prijava poslana: ${application.dateOfSubmission}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = " Naziv posla: $jobName \n Ime i prezime studenta: $studentName",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Edukacija: ${application.education}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onAction1Click(application) },
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Text("$name1 prijavu")
                }
                Button(
                    onClick = { onAction2Click(application) },
                    modifier = Modifier.padding(horizontal = 5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("$name2 prijavu")
                }
            }
        }
    }
}