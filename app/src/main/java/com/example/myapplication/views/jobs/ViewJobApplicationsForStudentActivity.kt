package com.example.myapplication.views.jobs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.views.HeaderUI
import com.example.myapplication.views.ListApplications
import com.example.myapplication.viewmodels.JobApplicationViewModel
import com.example.myapplication.views.jobs.ui.theme.MyApplicationTheme

class MyJobApplicationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyJobApplicationsScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MyJobApplicationsScreen(modifier: Modifier = Modifier, jobApplicationsViewModel: JobApplicationViewModel = viewModel()) {

    val applications by jobApplicationsViewModel.applications.observeAsState(emptyList())

    val userId = 2 // TODO("promjeniti na prijavljeni user id")
    LaunchedEffect(userId) {
        jobApplicationsViewModel.getApplicationsForStudent(userId)
    }
    Column(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderUI()

        ListApplications(applications, modifier = Modifier.weight(1f))
        //TODO("promjeniti na prijavljeni user id")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        ViewJobApplicationsForEmployerScreen()
    }
}