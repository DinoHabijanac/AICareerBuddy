package com.example.myapplication.views

import android.graphics.Paint
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.viewmodels.ViewCVViewModel
import com.example.myapplication.views.ui.theme.MyApplicationTheme
//import com.google.android.material.carousel.Arrangement

class ViewCVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ViewCVScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ViewCVScreen(
    modifier: Modifier = Modifier,
    viewCVViewModel: ViewCVViewModel = viewModel()
    // onRefresh: () -> Unit
){
    // val state by viewCVViewModel.state.collectAsState() // TODO
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .background(Color.Blue)
                .height(80.dp)
        ) {
            Text(
                text = "AI Career Buddy",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App logo",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp)
            )
        }
        // implementacija refresh butttona
        Button(
            onClick = {  }, // TODO implementacija buttona onClick
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Refresh CV")
        }
        Spacer(modifier = Modifier.height(16.dp))
        //when (){ //TODO
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        ViewCVScreen()
    }
}