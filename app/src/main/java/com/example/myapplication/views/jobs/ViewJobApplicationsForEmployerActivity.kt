package com.example.myapplication.views.jobs

import android.content.Intent
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.models.JobApplication
import com.example.myapplication.views.getLoggedUserId
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.ListApplications
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme

class ViewJobApplicationsForEmployerActivity : ComponentActivity() {

    private val applicationsViewModel: JobApplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val employerId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", 0)

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ViewJobApplicationsForEmployerScreen(
                        modifier = Modifier.padding(innerPadding),
                        applicationsViewModel,
                        onEditClick = { application ->
                            val prefs = getSharedPreferences("application_prefs", MODE_PRIVATE)
                            prefs.edit().putInt("applicationId", application.id ?: 0).apply()

                            startActivity(Intent(this, EditApplicationActivity::class.java))
                        },
                        onDeleteClick = { application ->
                            applicationsViewModel.deleteApplication(application.id ?: 0)
                            applicationsViewModel.getApplicationsForEmployer(employerId)

                            Toast.makeText(this, "Brisanje prijave...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ViewJobApplicationsForEmployerScreen(
    modifier: Modifier = Modifier,
    jobApplicationsViewModel: JobApplicationViewModel,
    onEditClick: (JobApplication) -> Unit,
    onDeleteClick: (JobApplication) -> Unit
) {

    val applications by jobApplicationsViewModel.applications.observeAsState(emptyList())

    val employerId = getLoggedUserId()
    LaunchedEffect(employerId) {
        jobApplicationsViewModel.getApplicationsForEmployer(3)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        ListApplications(applications, modifier = Modifier.weight(1f), onEditClick = onEditClick, onDeleteClick = onDeleteClick)
    }
}
