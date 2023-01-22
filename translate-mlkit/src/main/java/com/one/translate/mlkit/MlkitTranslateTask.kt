package com.one.translate.mlkit

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.one.coreapp.data.usecase.ResultState
import com.one.coreapp.data.usecase.isFailed
import com.one.coreapp.utils.extentions.resumeActive
import com.one.translate.TranslateTask
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class MlkitTranslateTask : TranslateTask {


    private val map: Map<String, String> = hashMapOf(
    )

    override suspend fun execute(param: TranslateTask.Param): ResultState<List<String>> = withContext(coroutineContext) {

        val sourceLanguage = map[param.inputCode] ?: param.inputCode

        val targetLanguage = map[param.outputCode] ?: param.outputCode


        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()


        val translator = Translation.getClient(options)


        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()


        val downloadState = suspendCancellableCoroutine<ResultState<List<String>>> { continuation ->

            translator.downloadModelIfNeeded(conditions).addOnSuccessListener {

                continuation.resumeActive(ResultState.Success(emptyList()))
            }.addOnFailureListener {

                continuation.resumeActive(ResultState.Failed(it))
            }
        }


        if (downloadState.isFailed()) {

            return@withContext downloadState
        }


        val translateStateList = param.text.map {

            async {
                execute(translator, it)
            }
        }.awaitAll()


        val stateError = translateStateList.find { it.isFailed() } as? ResultState.Failed


        if (stateError != null) {

            return@withContext ResultState.Failed(stateError.error)
        }

        return@withContext translateStateList.map {

            (it as ResultState.Success).data
        }.let {

            ResultState.Success(it)
        }
    }

    private suspend fun execute(translator: Translator, text: String): ResultState<String> {

        return suspendCancellableCoroutine { continuation ->

            translator.translate(text).addOnSuccessListener { translatedText ->

                continuation.resumeActive(ResultState.Success(translatedText))
            }.addOnFailureListener {

                continuation.resumeActive(ResultState.Failed(it))
            }
        }
    }
}