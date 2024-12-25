package com.sunayanpradhan.digitalinkrecognition

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.sunayanpradhan.digitalinkrecognition.StrokeManager.clear
import com.sunayanpradhan.digitalinkrecognition.StrokeManager.download
import com.sunayanpradhan.digitalinkrecognition.StrokeManager.recognize

class MainActivity : AppCompatActivity() {
    private lateinit var btnRecognize: Button
    private lateinit var btnClear: Button
    private lateinit var drawView: DrawView
    private lateinit var textView: TextView
    private lateinit var searchbtn:Button
    private lateinit var account:Button
    private lateinit var addFav: Button
    private val languages = listOf(
        "en-US",   // English
        "es-ES",   // Spanish
        "fr-FR",   // French
        "hi-IN",   // Hindi
        "te-IN",   // Telugu
        "ta-IN",   // Tamil
        "kn-IN"    // Kannada
        // Add more languages as needed
    )

    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btnRecognize = findViewById(R.id.buttonRecognize)
        btnClear = findViewById(R.id.buttonClear)
        drawView = findViewById(R.id.draw_view)
        textView = findViewById(R.id.textResult)
        searchbtn=findViewById(R.id.search)
        account=findViewById(R.id.account)
        addFav=findViewById(R.id.addFav)


        account.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        hideTitleBar()

        download(applicationContext)

        btnRecognize.setOnClickListener {
            StrokeManager.recognize(textView)
        }



        val languageSpinner: Spinner = findViewById(R.id.languageSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter
        val defaultLanguagePosition = 0
        languageSpinner.setSelection(defaultLanguagePosition)
        StrokeManager.setLanguage(languages[defaultLanguagePosition])
        StrokeManager.download(applicationContext, languages[defaultLanguagePosition])

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                StrokeManager.setLanguage(selectedLanguage)
                StrokeManager.download(applicationContext, selectedLanguage)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }
        searchbtn.setOnClickListener{
            StrokeManager.recognizeAndSearch(textView,this)
        }
        btnClear.setOnClickListener {
            drawView.clear()
            clear()
            textView.text = ""
        }

        addFav.setOnClickListener {
            addToFavourites(textView.text.toString())
            Toast.makeText(this, "Added to Favorites ❤️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideTitleBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    override fun onResume() {
        super.onResume()
        hideTitleBar()
    }

    override fun onPause() {
        super.onPause()
        hideTitleBar()
    }

    override fun onStop() {
        super.onStop()
        hideTitleBar()
    }



    private fun addToFavourites(text: String) {
        StrokeManager.favourites.add(text)
    }

}