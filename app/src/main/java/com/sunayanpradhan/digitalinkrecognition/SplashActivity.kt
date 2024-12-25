package com.sunayanpradhan.digitalinkrecognition

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Find the ImageView
        val splashLogo = findViewById<ImageView>(R.id.splashLogo)

        // Load the GIF using Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.splash) // Replace with the actual resource ID of your GIF
            .into(splashLogo)

        // Handler to navigate to the main activity after a delay
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 milliseconds delay (adjust as needed)
    }
}
