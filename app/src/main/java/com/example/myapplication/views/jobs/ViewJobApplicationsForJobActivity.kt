package com.example.myapplication.views.jobs

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.core.models.JobApplication
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.ListApplications
import com.example.myapplication.views.getJobId
import com.example.myapplication.views.getLoggedUserId
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme
import java.time.LocalDate


class ViewJobApplicationsForEmployerActivity : ComponentActivity() {

    private val applicationsViewModel: JobApplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ViewJobApplicationsForEmployerScreen(
                        modifier = Modifier.padding(innerPadding),
                        applicationsViewModel,
                        onAcceptClick = { application ->
                            application.status = "Prihvaćeno"
                            application.interviewDate = LocalDate.now().plusDays(3).toString()
                            applicationsViewModel.editApplication(application)

                            Toast.makeText(this, "Prijava prihvaćena", Toast.LENGTH_SHORT).show()
                            this.onResume()
                        },
                        onRejectClick = { application ->
                            application.status = "Odbijeno"
                            application.interviewDate = null
                            application.employerId = null
                            applicationsViewModel.editApplication(application)

                            Toast.makeText(this, "Prijava odbijena", Toast.LENGTH_SHORT).show()
                            this.onResume()
                        }
                    )
                }
            }
        }
    }
    override fun onResume(){
        super.onResume()
        val jobId = getSharedPreferences("job_prefs", MODE_PRIVATE).getInt("jobId", 0)
        applicationsViewModel.getApplicationsForJob(jobId)
    }
}

@Composable
fun ViewJobApplicationsForEmployerScreen(
    modifier: Modifier = Modifier,
    jobApplicationsViewModel: JobApplicationViewModel,
    onAcceptClick: (JobApplication) -> Unit,
    onRejectClick: (JobApplication) -> Unit
) {
    val applications by jobApplicationsViewModel.applications.observeAsState(emptyList())

    val jobId = getJobId()

    LaunchedEffect(Unit) {
        jobApplicationsViewModel.getApplicationsForJob(jobId)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        ListApplications(
            applications,
            onAction1Click = onAcceptClick,
            onAction2Click = onRejectClick,
            "Prihvati",
            "Odbij"
        )
    }
}