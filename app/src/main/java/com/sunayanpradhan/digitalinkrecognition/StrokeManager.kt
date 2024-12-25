package com.sunayanpradhan.digitalinkrecognition

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.MlKitException
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.*
import java.lang.ref.WeakReference
import java.net.URLEncoder
import java.util.*

object StrokeManager {
    private var contextRef: WeakReference<Context>? = null
    private var model: DigitalInkRecognitionModel? = null
    private var inkBuilder = Ink.builder()
    private var strokeBuilder: Ink.Stroke.Builder? = null
    private lateinit var textToSpeech: TextToSpeech
    private var selectedLanguageTag: String = "en-US" // Default language
    private var searchHistory: MutableList<String> = mutableListOf() // Store search history
    public var favourites: MutableList<String> = mutableListOf()

    fun setLanguage(languageTag: String) {
        selectedLanguageTag = languageTag
    }

    fun addNewTouchEvent(event: MotionEvent) {
        val x = event.x
        val y = event.y
        val t = System.currentTimeMillis()
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                strokeBuilder = Ink.Stroke.builder()
                strokeBuilder!!.addPoint(Ink.Point.create(x, y, t))
            }
            MotionEvent.ACTION_MOVE -> strokeBuilder!!.addPoint(Ink.Point.create(x, y, t))
            MotionEvent.ACTION_UP -> {
                strokeBuilder!!.addPoint(Ink.Point.create(x, y, t))
                inkBuilder.addStroke(strokeBuilder!!.build())
                strokeBuilder = null
            }
        }
    }

    fun download(context: Context, languageTag: String = selectedLanguageTag) {
        contextRef = WeakReference(context)
        val modelIdentifier = try {
            DigitalInkRecognitionModelIdentifier.fromLanguageTag(languageTag)
        } catch (e: MlKitException) {
            Log.e(ContentValues.TAG, "Error obtaining model identifier: $e")
            null
        }

        modelIdentifier?.let {
            model = DigitalInkRecognitionModel.builder(it).build()
            val remoteModelManager = RemoteModelManager.getInstance()
            remoteModelManager
                .download(model!!, DownloadConditions.Builder().build())
                .addOnSuccessListener {
                    Log.i(ContentValues.TAG, "Model downloaded")
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error downloading model: $e")
                }
        }

        textToSpeech = TextToSpeech(contextRef?.get()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.forLanguageTag(languageTag))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(ContentValues.TAG, "TTS language is not supported.")
                }
            } else {
                Log.e(ContentValues.TAG, "Error initializing TTS.")
            }
        }
    }

    fun recognize(textView: TextView) {
        val recognizer = DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(
                model!!
            ).build()
        )
        val ink = inkBuilder.build()
        recognizer.recognize(ink)
            .addOnSuccessListener { result ->
                val recognizedText = result.candidates.firstOrNull()?.text ?: ""
                textView.text = recognizedText

                // Add recognized text to search history
                addToSearchHistory(recognizedText)

                speakText(recognizedText)
            }
            .addOnFailureListener { e ->
                Log.e(
                    ContentValues.TAG,
                    "Error during recognition: $e"
                )
            }
    }

    private fun searchOnGoogle(query: String, context: Context) {
        val searchUrl = "https://www.google.com/search?q=${URLEncoder.encode(query, "UTF-8")}"

        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, query)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e(ContentValues.TAG, "No activity found to handle web search intent: $e")
        }
    }

    fun recognizeAndSearch(textView: TextView, context: Context) {
        val recognizer = DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(
                model!!
            ).build()
        )
        val ink = inkBuilder.build()
        recognizer.recognize(ink)
            .addOnSuccessListener { result ->
                val recognizedText = result.candidates.firstOrNull()?.text ?: ""
                textView.text = recognizedText
                speakText(recognizedText)

                // Run UI updates on the main thread
                (context as? AppCompatActivity)?.runOnUiThread {
                    // Add a delay before searching (e.g., 2 seconds)
                    Handler().postDelayed({
                        // Add search functionality here
                        searchOnGoogle(recognizedText, context)
                    }, 2000)
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    ContentValues.TAG,
                    "Error during recognition: $e"
                )
            }
    }

    private fun speakText(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun clear() {
        inkBuilder = Ink.builder()
    }

    // Method to add recognized text to search history
    private fun addToSearchHistory(text: String) {
        searchHistory.add(text)
    }

    // Method to retrieve search history
    fun getSearchHistory(): List<String> {
        return searchHistory
    }

    fun retrieveFavourites(): List<String> {
        return favourites
    }
}
