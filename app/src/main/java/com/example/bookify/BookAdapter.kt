package com.example.bookify

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(private val bookList: ArrayList<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val tvSubject: TextView = itemView.findViewById(R.id.tvBookSubject)
        val ivImage: ImageView = itemView.findViewById(R.id.ivBookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentBook = bookList[position]
        holder.tvTitle.text = currentBook.title
        holder.tvAuthor.text = "Yazar: ${currentBook.author}"
        holder.tvSubject.text = currentBook.subject

        val ratingBar = holder.itemView.findViewById<RatingBar>(R.id.rbListRating)


        ratingBar.rating = currentBook.rating


        if (currentBook.imageUrl.isNotEmpty()) {
            try {
                if (!currentBook.imageUrl.startsWith("http")) {
                    val decodedString = Base64.decode(currentBook.imageUrl, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    holder.ivImage.setImageBitmap(decodedByte)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)


            intent.putExtra("bookTitle", currentBook.title)
            intent.putExtra("bookAuthor", currentBook.author)
            intent.putExtra("bookSubject", currentBook.subject)
            intent.putExtra("bookImage", currentBook.imageUrl)
            intent.putExtra("bookRating", currentBook.rating)


            holder.itemView.context.startActivity(intent)
        }


        holder.itemView.setOnLongClickListener {
            val builder = android.app.AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Kitabı Sil")
            builder.setMessage("${currentBook.title} adlı kitabı silmek istiyor musun?")


            builder.setPositiveButton("Evet") { dialog, which ->
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                db.collection("Books").document(currentBook.id)
                    .delete()
                    .addOnSuccessListener {

                        bookList.removeAt(holder.adapterPosition)


                        notifyItemRemoved(holder.adapterPosition)


                        android.widget.Toast.makeText(holder.itemView.context, "Kitap silindi!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {

                        android.widget.Toast.makeText(holder.itemView.context, "Silinemedi: ${it.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                    }
            }


            builder.setNegativeButton("Hayır", null)

            builder.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}