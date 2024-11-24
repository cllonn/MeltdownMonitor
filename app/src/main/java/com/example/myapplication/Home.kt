package com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import kotlin.collections.List
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import androidx.compose.runtime.*
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Home(context: Context) {
    val meltdownList = remember { mutableStateListOf<Meltdown>() }
    val database = FirebaseDatabase.getInstance().getReference("/meltdownMonitorDatabase/SensorData")
    val query = database.child("entries")
        .orderByKey()  // Order by the push-generated key (chronologically ordered)
        .limitToLast(10)     // Initial data load
    LaunchedEffect(Unit) {
        // Fetch all existing data without processing
        database.limitToLast(30).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val key = child.key
                    val meltdown = fromFirebaseDocument(child)
                    if (meltdown!=null && meltdown.temp!=null) {
                        meltdown.key = key
                        meltdownList.add(meltdown)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDebug", "Database Error: ${error.message}")
            }
        })

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                fetchSingleNode( database, meltdownList, context)
                handler.postDelayed(this, 1 * 60 * 1000) // 5 minutes in milliseconds
            }
        }
        handler.postDelayed(runnable, 1 * 60 * 1000)
    }



    Box(modifier = Modifier.padding(vertical = 120.dp, horizontal = 20.dp)){
        historyText("Yousif")
    }
    RecyclerView(meltdownList)

}

private fun fetchSingleNode(database: DatabaseReference, meltdownList: MutableList<Meltdown>, context: Context) {
    database.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDataChange(snapshot: DataSnapshot) {
            for (child in snapshot.children) {
                val key = child.key
                val meltdown = fromFirebaseDocument(child)
                if (meltdown!=null && meltdown.temp!=null && meltdown.meltdownType!=null) {
                    meltdown.key = key
                    handleNewMeltdownData(meltdown, database, context);
                    meltdownList.add(meltdown)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle any potential errors here
            Log.e("FirebaseError", "Error fetching data: ${error.message}")
        }
    })
}


@Composable
fun historyText(name: String) {
    Text(
        text = "$name's history of meltdowns",
        color = Color.Red,
        style = MaterialTheme.typography.headlineMedium
    )
}

private fun fromFirebaseDocument(snapshot: DataSnapshot): Meltdown? {
    // Manually map fields if necessary
    val data = snapshot.value as? Map<*, *> ?: return null

    return Meltdown(
        key = snapshot.key,
        date = data["Date"] as? String ?: data["date"] as? String,
        gsr = (data["GSR"] as? Long)?.toInt() ?: (data["gsr"] as? Long)?.toInt(), // Convert Long to Int
        hr = (data["HR"] as? Long)?.toInt() ?: (data["hr"] as? Long)?.toInt(), // Convert Long to Int
        meltdownType = data["Meltdown Type"] as? String ?: data["meltdowntype"] as? String,
        temp = data["TEMP"] as? Double ?: data["temp"] as? Double,
        time = data["Time"] as? String ?: data["time"] as? String
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun handleNewMeltdownData( meltdown: Meltdown, database: DatabaseReference, context: Context) {
    // Skip already processed data
    if (!meltdown.meltdownType.isNullOrBlank()) {
        Log.d("FirebaseDebug", "Meltdown Type already present for key: ${meltdown.key}")
        return
    }

    // Process new data
    if (meltdown.hr != null && meltdown.gsr != null && meltdown.temp != null) {
        val inputData = preprocessInput(
            meltdown.date ?: "",
            meltdown.time ?: "",
            meltdown.hr,
            meltdown.gsr,
            meltdown.temp.toFloat()
        )
        val modelHelper = TFLiteModelHelper(context)
        val predictions = modelHelper.checkForMeltdown(meltdown)
        val predictedMeltdownType = if (!predictions) "No" else "Yes"

        modelHelper.close()

        if (predictedMeltdownType.equals("Yes", true)) {
            generateAlarm(meltdown, context)
        }

        // Update Meltdown Type in Firebase
        val updatedMeltdown = meltdown.copy(meltdownType = predictedMeltdownType)
        database.child(meltdown.key!!).setValue(updatedMeltdown)
            .addOnSuccessListener {
                Log.d("FirebaseDebug", "Successfully updated Meltdown Type for key: ${meltdown.key}")
            }
            .addOnFailureListener { error ->
                Log.e("FirebaseDebug", "Failed to update Meltdown Type for key: ${meltdown.key}. Error: ${error.message}")
            }
    } else {
        Log.e("FirebaseDebug", "Invalid data for prediction: ${meltdown.key}")
    }
}



@Composable
fun RecyclerView(meltdowns: List<Meltdown>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
        items(meltdowns) { meltdown ->
            ListItemName(meltdown = meltdown)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun generateAlarm(meltdown: Meltdown, context: Context)
{
    val calendar: Calendar = Calendar.getInstance()
    if (Build.VERSION.SDK_INT >= 23) {
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            LocalDateTime.now().hour,
            LocalDateTime.now().minute+1,
            0
        )
    } else {
        calendar.set(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            LocalDateTime.now().hour,
            LocalDateTime.now().minute+1, 0
        )
    }
    setAlarm(calendar.timeInMillis, context)
}


private fun setAlarm(timeInMillis: Long, context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyAlarm::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.setRepeating(
        AlarmManager.RTC,
        timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
    Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show()
}

private class MyAlarm : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Log.d("Alarm Bell", "Alarm just fired")
    }
}

@Composable
fun ListItemName(meltdown: Meltdown){
    val expanded = remember { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        if(expanded.value) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(color = Color.Red,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)){
        Column (modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()){
            Row {
                Column (modifier = Modifier
                    .weight(1f)){
                    Text(text = meltdown.time.toString(), color = Color.White)
                    Text(text = meltdown.temp.toString(), color = Color.White, style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ))
                }

                OutlinedButton(onClick = {expanded.value = !expanded.value}) {
                    Text(if(expanded.value)"Show less" else "Show more", color = Color.White)

                }
            }

            if (expanded.value){

                Column(modifier = Modifier.padding(
                    bottom = extraPadding.coerceAtLeast(0.dp)
                )) {
                    Text(text = "GSR" + meltdown.gsr.toString(), color = Color.White)
                }
                Column(modifier = Modifier.padding(
                    bottom = extraPadding.coerceAtLeast(0.dp)
                )) {
                    Text(text ="HR" + meltdown.hr.toString(), color = Color.White)
                }
                Column(modifier = Modifier.padding(
                    bottom = extraPadding.coerceAtLeast(0.dp)
                )) {
                    Text(text ="time" + meltdown.time, color = Color.White)
                }
            }
        }

    }
}


