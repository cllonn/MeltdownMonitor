package com.example.myapplication

sealed class Screens (val screens: String){
    object Home: Screens("Home")
    object GetVest: Screens("getvest")
    object SignIn: Screens("SignIn")
    object SignUp: Screens("SignUp")


}