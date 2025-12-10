package com.example.myapplication.views.jobs

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.models.JobListing
import com.example.myapplication.viewmodels.JobsViewModel
import com.example.myapplication.views.ui.theme.MyApplicationTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//ova aktivnost daje mogućnost poslodavcu da kreira oglas
class CreateJobActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AddJobsScreen(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun AddJobsScreen(modifier: Modifier = Modifier, jobsViewModel: JobsViewModel = viewModel()) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var employerId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var listingExpires by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf("") }
    var payPerHour by remember { mutableStateOf("") }

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
            Text(
                "Unesi novi oglas za posao",
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Naziv oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Opis oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(text = "Kategorija oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(text = "Lokacija oglasa") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            DateOnlyPicker(
                label = "Datum isteka oglasa",
                initialIsoDate = listingExpires.ifBlank { null },
                onDateSelected = { iso -> listingExpires = iso }
            )
            OutlinedTextField(
                value = terms,
                onValueChange = { terms = it },
                label = { Text(text = "Uvjeti oglasa (komunikativnost, spremnost ...)") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            OutlinedTextField(
                value = payPerHour,
                onValueChange = { payPerHour = it },
                label = { Text(text = "Plaća po satu") },
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                modifier = Modifier.width(220.dp),
                onClick = {
                    try {
                        if(name.isBlank() || description.isBlank() || category.isBlank() || location.isBlank() || listingExpires.isBlank() || terms.isBlank() || payPerHour.isBlank()){
                            Toast.makeText(context, "Sva polja moraju biti popunjena", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val job = JobListing(
                            name = name,
                            description = description,
                            category = category,
                            location = location,
                            listingExpires = LocalDate.parse(listingExpires),
                            terms = terms,
                            payPerHour = payPerHour.toInt(),
                            employerId = 2 // ISPRAVI NAKON PRIJAVE
                        )
                        jobsViewModel.uploadJob(job)
                        if(jobsViewModel.uploadState.value == "Uspješno dodan oglas"){
                            name = ""
                            description = ""
                            category = ""
                            location = ""
                            listingExpires = ""
                            terms = ""
                            payPerHour = ""
                        }
                        Toast.makeText(context, jobsViewModel.uploadState.value, Toast.LENGTH_SHORT).show()
                        Log.d("Debugiranje!",jobsViewModel.uploadState.value.toString())
                    }catch (e: Exception){
                        Toast.makeText(context, "Greška pri dodavanju oglasa - ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.d("Debugiranje!",e.message.toString())
                    }
                },
            ) {
                Text("Kreiraj oglas")
            }
        }
    }
}

@Composable
fun DateOnlyPicker(
    label: String = "Datum",
    initialIsoDate: String? = null,
    onDateSelected: (isoDate: String) -> Unit = {}
) {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    val initialMillis = remember(initialIsoDate) {
        initialIsoDate?.let {
            try {
                LocalDate.parse(it, formatter)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } catch (e: Exception) {
                null
            }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    var showDialog by remember { mutableStateOf(false) }
    var displayValue by remember { mutableStateOf(initialIsoDate ?: "") }

    OutlinedTextField(
        value = displayValue,
        onValueChange = { /* read-only */ },
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        trailingIcon = {
            TextButton(onClick = { showDialog = true }) {
                Text("Odaberi")
            }
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val localDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val iso = localDate.format(formatter)
                        displayValue = iso
                        onDateSelected(iso)
                    }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddJobsScreenPreview() {
    MyApplicationTheme {
        AddJobsScreen()
    }
}