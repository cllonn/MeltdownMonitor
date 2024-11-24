package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun GetVest(){

    Screen()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(){
    val context = LocalContext.current.applicationContext
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 120.dp, horizontal = 8.dp)
    ) {
        Column {
            var expanded by remember { mutableStateOf(false) }
            var selectedSize by remember { mutableStateOf("Select size") }
            val sizes = listOf("Small", "Medium", "Large")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
                    .clickable { expanded = !expanded }
            ) {
                Text(text = selectedSize, modifier = Modifier.padding(8.dp))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                sizes.forEach { size ->
                    DropdownMenuItem(
                        text = { Text(text = size) },
                        onClick = {
                            selectedSize = size
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            var color by remember { mutableStateOf("") }
            TextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Enter your preferred color") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            var selectedPaymentMethod by remember { mutableStateOf("Card") }

            Text(text = "Choose Payment Method")
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                RadioButton(
                    selected = selectedPaymentMethod == "Card",
                    onClick = { selectedPaymentMethod = "Card"
                    }
                )
                Text(text = "Card", modifier = Modifier.padding(start = 8.dp))

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = selectedPaymentMethod == "Cash",
                    onClick = { selectedPaymentMethod = "Cash" }
                )
                Text(text = "Cash", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Size: $selectedSize, Color: $color, Payment: $selectedPaymentMethod",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }}



@Preview(showBackground = true)
@Composable
fun GreetingsPrev(){
    MyApplicationTheme {
        Surface(modifier = Modifier, color = Color.White) {
            Screen()
        }



    }
}