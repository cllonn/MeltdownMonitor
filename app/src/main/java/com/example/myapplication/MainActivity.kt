package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MyApplicationTheme {
                Surface(
                                   ) {
                    NavDrawer()
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavDrawer() {
    val navigationController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current.applicationContext

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true, drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Text("")
                }
                Divider()
                NavigationDrawerItem(label = { Text("Home", color = Color.Red) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.Red
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.Home.screens) {
                            popUpTo(0)
                        }

                    })
                NavigationDrawerItem(label = { Text("Get new vest", color = Color.Red) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "GetVest",
                            tint = Color.Red
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.GetVest.screens) {
                            popUpTo(0)
                        }

                    })
                NavigationDrawerItem(label = { Text("Log out", color = Color.Red) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "SignIn",
                            tint = Color.Red
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navigationController.navigate(Screens.SignIn.screens) {
                            popUpTo(0)
                        }

                    })
                NavigationDrawerItem(label = { Text(text = "Help", color = Color.Red) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Red
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        Toast.makeText(
                            context,
                            "if you need help please contact us +9710000000",
                            Toast.LENGTH_SHORT
                        ).show()

                    })
                NavigationDrawerItem(label = { Text(text = "About us", color = Color.Red) },
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.Red
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        Toast.makeText(context, " info about the vest ...", Toast.LENGTH_LONG)
                            .show()

                    })
            }
        }) {
        Scaffold(
            topBar = {
                    TopAppBar(
                        title = { Text(text = "Please Choose", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Red,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                            }
                        }
                    )
            }
        ) {
            NavHost(navController = navigationController, startDestination = Screens.SignIn.screens) {

                composable(Screens.Home.screens) {
                    Home(context)
                }
                composable(Screens.GetVest.screens) {
                    GetVest()
                }
                composable(Screens.SignIn.screens) {
                    SignIn(navigationController)
                }
                composable(Screens.SignUp.screens) {
                    SignUp(navigationController)
                }
            }

        }


    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Surface(modifier = Modifier, color = Color.Red) {
            NavDrawer()
        }
    }
}
fun preprocessInput(date: String, time: String, hr: Int, gsr: Int, temp: Float): FloatArray {
    val dateTime = "$date $time"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val timestamp = dateFormat.parse(dateTime)?.time?.toFloat() ?: 0.0f

    // Normalize input features if required
    val normalizedHR = hr / 200.0f       // Example normalization
    val normalizedGSR = gsr / 5000.0f   // Example normalization
    val normalizedTemp = (temp - 20.0f) / 10.0f // Example normalization
    val normalizedTimestamp = timestamp / 1e13f // Example scaling for timestamps

    return floatArrayOf(normalizedTimestamp, normalizedHR, normalizedGSR, normalizedTemp)
}