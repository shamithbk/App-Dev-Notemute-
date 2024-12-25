package com.sunayanpradhan.digitalinkrecognition

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.core.view.View
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sunayanpradhan.digitalinkrecognition.databinding.ActivityAccountBinding
import de.hdodenhof.circleimageview.CircleImageView

class account : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var storage: StorageReference
    private lateinit var selectedImageUri: Uri

    companion object {
        const val PICK_IMAGE_REQUEST =1
        const val PREF_PHOTO_URI = "pref_photo_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance().reference

        val btn = findViewById<Button>(R.id.main)
        val change = findViewById<Button>(R.id.button)
        val save = findViewById<Button>(R.id.button3)
        val imageView = findViewById<CircleImageView>(R.id.img) // Use CircleImageView here

        val intent = intent
        val name = intent.getStringExtra("Name")
        val user = findViewById<TextView>(R.id.userdetails)
        user.text = name

        change.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        save.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Get the saved photo URI from SharedPreferences
        val savedPhotoUriString = sharedPreferences.getString(PREF_PHOTO_URI, null)
        if (savedPhotoUriString != null) {
            val savedPhotoUri = Uri.parse(savedPhotoUriString)
            selectedImageUri = Uri.parse(savedPhotoUriString)
            imageView.setImageURI(selectedImageUri)
        }
    }


    private fun uploadImageToFirebaseStorage() {
        val imagesRef = storage.child("images/${selectedImageUri.lastPathSegment}")

        val uploadTask = imagesRef.putFile(selectedImageUri)

        uploadTask.addOnSuccessListener {
            // Image uploaded successfully. Now you can do whatever you want.
            // For example, you can display a toast message.
            // You may also want to store the image URL in the Realtime Database.
        }.addOnFailureListener {
            // Handle unsuccessful uploads
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            val imageView = findViewById<CircleImageView>(R.id.img) // Use CircleImageView here
            imageView.setImageURI(selectedImageUri)
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            // Save the selected photo URI to SharedPreferences
            sharedPreferences.edit().putString(PREF_PHOTO_URI, selectedImageUri.toString()).apply()
        }
    }
    fun onSearchHistoryClicked(view: android.view.View) {
        val intent = Intent(this, SearchHistoryActivity::class.java)
        startActivity(intent)
    }

    fun onFavClicked(view: android.view.View){
        val intent = Intent(this, FavouritesActivity::class.java)
        startActivity(intent)
    }
}
