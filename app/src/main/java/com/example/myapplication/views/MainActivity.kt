package com.example.myapplication.views

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding),
                        onResumeClick = {
                            startActivity(Intent(this, UploadResumeActivity::class.java))
                        },
                        onViewJobsClick = {
                            startActivity(Intent(this, JobActivity::class.java))
                        },
                        onCreateJobsClick = {
                            startActivity(Intent(this, CreateJobActivity::class.java))
                        },
                        onViewCVClick = {
                            startActivity(Intent(this, ViewCVActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, onResumeClick: () -> Unit = {}, onViewJobsClick: () -> Unit = {}, onCreateJobsClick: ()->Unit = {}, onViewCVClick: ()-> Unit = {}) {
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

        Spacer(modifier = Modifier.height(32.dp))

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
            Spacer(modifier = Modifier.height(height = 12.dp))
            Button(onClick = onViewCVClick, modifier = Modifier.width(220.dp)) {
                Text("Vidi životopis")
            }
        }
    }
}
