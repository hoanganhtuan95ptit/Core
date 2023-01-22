package com.one.detect.mlkit

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.one.detect.mlkit.MlkitDetectTask

class DevanagariMlkitDetectTask : MlkitDetectTask() {

    override fun inputCodeSupport(): List<String> = listOf("hi", "mr", "ne", "sa")

    override fun process(inputImage: InputImage): Task<Text> {
        return TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build()).process(inputImage)
    }
}