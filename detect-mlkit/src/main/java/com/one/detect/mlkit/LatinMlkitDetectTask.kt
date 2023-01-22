package com.one.detect.mlkit

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class LatinMlkitDetectTask : MlkitDetectTask() {

    override fun inputCodeSupport(): List<String> = listOf()

    override fun process(inputImage: InputImage): Task<Text> {
        return TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS).process(inputImage)
    }

}