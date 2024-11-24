package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun SignUp(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference.child("users")

    val annotatedText = buildAnnotatedString {
        append("Already have an account? ")

        pushStringAnnotation(tag = "SIGN_IN", annotation = "sign_in")
        withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
            append("Sign In")
        }
        pop()
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun registerUser() {
        if (email.isNotBlank() && password.isNotBlank() && isValidEmail(email)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val userMap = mapOf(
                            "username" to username,
                            "email" to email,
                            "dateOfBirth" to dateOfBirth,
                            "gender" to gender
                        )

                        database.child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screens.Home.screens)
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Please enter valid email and password", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFE0100))
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.navigate("/SignIn")
                        Toast.makeText(context, "Back Icon Clicked", Toast.LENGTH_SHORT).show()
                    }
            )

            Text(
                text = "Sign up",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Form fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                value = email,
                onValueChange = {
                    email = it
                    isValidEmail = isValidEmail(it)
                },
                placeholder = { Text("Email*") },
                isError = !isValidEmail,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    capitalization = KeyboardCapitalization.None
                )
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Choose password*") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Choose username*") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                placeholder = { Text("Date of birth*") }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = gender,
                onValueChange = { gender = it },
                placeholder = { Text("Gender") }
            )

            Text(
                text = "By proceeding you also agree to the Terms of Service and Privacy Policy",
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 16.dp)
            )

            Button(
                onClick = { registerUser() },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSystemInDarkTheme()) Color(0xFFFE0100) else Color((0xFFFE0100)),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign up")
            }

            ClickableText(
                text = annotatedText,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "SIGN_IN", start = offset, end = offset)
                        .firstOrNull()?.let {
                            navController.navigate(Screens.SignIn.screens)
                            Toast.makeText(context, "Sign In clicked", Toast.LENGTH_SHORT).show()
                        }
                }
            )
        }
    }
}
