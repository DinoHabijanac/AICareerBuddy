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
import com.example.core.models.JobApplication
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.ListApplications
import com.example.myapplication.views.getLoggedUserId
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme

class MyJobApplicationsActivity : ComponentActivity() {

    private val applicationsViewModel: JobApplicationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        applicationsViewModel.uploadState.observe(this) { state ->
            if (state != null && state.contains("Uspješno obrisana prijava")) {
                val userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", 0)
                applicationsViewModel.getApplicationsForStudent(userId)
                Toast.makeText(this, "Prijava obrisana", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyJobApplicationsScreen(
                        modifier = Modifier.padding(innerPadding),
                        jobApplicationsViewModel = applicationsViewModel,
                        onEditClick = { application ->
                            val prefs = getSharedPreferences("application_prefs", MODE_PRIVATE)
                            prefs.edit().putInt("applicationId", application.id ?: 0).apply()

                            startActivity(Intent(this, EditApplicationActivity::class.java))
                        },
                        onDeleteClick = { application ->
                            applicationsViewModel.deleteApplication(application.id ?: 0)
                            Toast.makeText(this, "Brisanje prijave...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("userId", 0)
        applicationsViewModel.getApplicationsForStudent(userId)
    }
}

@Composable
fun MyJobApplicationsScreen(
    modifier: Modifier = Modifier,
    jobApplicationsViewModel: JobApplicationViewModel,
    onEditClick: (JobApplication) -> Unit,
    onDeleteClick: (JobApplication) -> Unit
) {
    val applications by jobApplicationsViewModel.applications.observeAsState(emptyList())
    val userId = getLoggedUserId()

    LaunchedEffect(userId) {
        jobApplicationsViewModel.getApplicationsForStudent(userId)
    }

    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        ListApplications(applications, modifier = Modifier.weight(1f), onEditClick = onEditClick, onDeleteClick = onDeleteClick)
    }
}