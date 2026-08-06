package com.example.bookify

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {

    private lateinit var ivSelectedImage: ImageView
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etSubject: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    private var selectedBitmap: Bitmap? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        etTitle = findViewById(R.id.etBookTitle)
        etAuthor = findViewById(R.id.etBookAuthor)
        etSubject = findViewById(R.id.etBookSubject)
        btnSave = findViewById(R.id.btnSaveBook)
        progressBar = findViewById(R.id.progressBar)
        val ratingBar = findViewById<RatingBar>(R.id.rbDetailRating)



        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Uri'yi Bitmap'e çeviriyoruz
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    selectedBitmap = BitmapFactory.decodeStream(inputStream)
                    ivSelectedImage.setImageBitmap(selectedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        ivSelectedImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 2. Kaydetme
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val author = etAuthor.text.toString().trim()
            val subject = etSubject.text.toString().trim()
            val currentRating = ratingBar.rating


            if (title.isEmpty() || author.isEmpty() || subject.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedBitmap == null) {
                Toast.makeText(this, "Lütfen bir resim seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            kaydet(title, author, subject, currentRating)
        }
    }

    private fun kaydet(title: String, author: String, subject: String, rating: Float) {
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false


        val base64Image = bitmapToSmallBase64(selectedBitmap!!)
        val userEmail = auth.currentUser!!.email!!.toString()

        val bookMap = hashMapOf<String, Any>()
        bookMap.put("title", title)
        bookMap.put("author", author)
        bookMap.put("subject", subject)
        bookMap.put("rating", rating)
        bookMap.put("imageUrl", base64Image)
        bookMap.put("uploadDate", com.google.firebase.Timestamp.now())

        bookMap.put("userEmail", userEmail)
        db.collection("Books").add(bookMap)


            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Kitap Başarıyla Eklendi!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(this, "Hata: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun bitmapToSmallBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}