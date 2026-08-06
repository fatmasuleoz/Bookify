package com.example.bookify // <-- Senin paket ismin buraya gelecek

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DiscoverActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var bookList: ArrayList<Book>
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Keşfet 🌍"


        db = FirebaseFirestore.getInstance()
        bookList = ArrayList()


        val recyclerView = findViewById<RecyclerView>(R.id.rvDiscover)
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(bookList)
        recyclerView.adapter = bookAdapter


        tumKitaplariGetir()
    }

    private fun tumKitaplariGetir() {

        db.collection("Books")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        bookList.clear()

                        for (document in value.documents) {
                            val book = document.toObject(Book::class.java)
                            if (book != null) {
                                bookList.add(book)
                            }
                        }
                        bookAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}