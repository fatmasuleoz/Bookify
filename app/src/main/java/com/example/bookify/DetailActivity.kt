package com.example.bookify

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide



class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kitap Detayı"


        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvAuthor = findViewById<TextView>(R.id.tvDetailAuthor)
        val tvSubject = findViewById<TextView>(R.id.tvDetailSubject)
        val ivImage = findViewById<ImageView>(R.id.ivDetailImage)
        val ratingBar = findViewById<RatingBar>(R.id.rbDetailRating)
        val btnWebSearch = findViewById<Button>(R.id.btnWebSearch)


        val gelenBaslik = intent.getStringExtra("bookTitle")
        val gelenYazar = intent.getStringExtra("bookAuthor")
        val gelenKonu = intent.getStringExtra("bookSubject")
        val gelenPuan = intent.getFloatExtra("bookRating", 0.0f)

        ratingBar.rating = gelenPuan




        tvTitle.text = gelenBaslik


        if (tvAuthor != null) tvAuthor.text = gelenYazar
        if (tvSubject != null) tvSubject.text = gelenKonu

        val gelenResimUrl =
            intent.getStringExtra("bookImage")

        Glide.with(this)
            .load(gelenResimUrl)
            .into(ivImage)

        btnWebSearch.setOnClickListener{


            val aramaMetni = "$gelenBaslik $gelenYazar kitap"


            val url = "https://www.google.com/search?q=$aramaMetni"


            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(url)


            startActivity(intent)
        }




    }




    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}