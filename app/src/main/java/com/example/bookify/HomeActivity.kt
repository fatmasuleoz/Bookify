package com.example.bookify

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookList: ArrayList<Book>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        supportActionBar?.title = "Kütüphanem"

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvEmptyState = findViewById(R.id.tvEmptyState)
        rvBooks = findViewById(R.id.rvBooks)


        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.setHasFixedSize(true)

        bookList = ArrayList()
        bookAdapter = BookAdapter(bookList)
        rvBooks.adapter = bookAdapter


        verileriGetir()

        val btnDiscover = findViewById<Button>(R.id.btnGoDiscover)
        btnDiscover.setOnClickListener {
            val intent = Intent(this, DiscoverActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {

                val intent = Intent(this, AddBookActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_logout -> {

                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun verileriGetir() {

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()


        if (auth.currentUser != null) {
            val currentEmail = auth.currentUser!!.email!!.toString()

            db.collection("Books")
                .whereEqualTo("userEmail", currentEmail)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        android.widget.Toast.makeText(this, "Hata: ${error.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                    } else {
                        if (snapshot != null) {
                            bookList.clear()

                            for (document in snapshot.documents) {
                                val book = document.toObject(Book::class.java)
                                if (book != null) {
                                    book.id = document.id
                                    bookList.add(book)
                                }
                            }
                            bookAdapter.notifyDataSetChanged()

                            val tvEmptyState = findViewById<android.widget.TextView>(R.id.tvEmptyState)

                            if (bookList.size > 0) {

                                tvEmptyState.visibility = android.view.View.GONE
                            } else {

                                tvEmptyState.visibility = android.view.View.VISIBLE
                            }
                        }
                    }
                }
        }
    }
}