package com.example.myapplication

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class TFLiteModelHelper(context: Context) {

    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(inputData: FloatArray): FloatArray {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()

        val input = Array(1) { inputData }
        val output = Array(1) { FloatArray(outputShape[1]) }

        interpreter.run(input, output)

        return output[0]
    }

    fun checkForMeltdown(meltdown: Meltdown): Boolean {
        // Check if any of the conditions for meltdown are met
        return when {
            // Condition 1: HR > 100 bpm and GSR > 250 µS and Temp > 37.5°C
            meltdown.hr!! > 100 && meltdown.gsr!! > 250 && meltdown.temp!! > 37.5 -> true

            // Condition 2: HR > 120 bpm OR GSR > 300 µS OR Temp > 38°C
            meltdown.hr > 120 || meltdown.gsr!! > 300 || meltdown.temp!! > 38 -> true

            // Condition 3: HR > 90 bpm OR GSR > 200 µS AND Temp > 37°C
            meltdown.hr > 90 || (meltdown.gsr > 200 && meltdown.temp > 37) -> true

            // If none of the above conditions are met
            else -> false
        }
    }

    fun close() {
        interpreter.close()
    }
}
