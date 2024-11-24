package com.example.myapplication;

import com.google.firebase.database.PropertyName

data class Meltdown(
    var key: String? = null,
    @PropertyName("Date") val date: String? = null,

    @PropertyName("GSR") val gsr: Int? = null,
    @PropertyName("HR") val hr: Int? = null,
    @PropertyName("Meltdown Type") var meltdownType: String? = null, // Corrected property name
    @PropertyName("TEMP") val temp: Double? = null, // Changed to Double to match TEMP value
    @PropertyName("Time") val time: String? = null
)


