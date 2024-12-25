package com.sunayanpradhan.digitalinkrecognition
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunayanpradhan.digitalinkrecognition.FavouriteItem
import java.text.SimpleDateFormat
import java.util.*

class FavouritesAdapter(private val favourites: List<FavouriteItem>) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = favourites[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val queryTextView: TextView = itemView.findViewById(R.id.queryTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)

        private lateinit var query: String

        init {
            queryTextView.setOnClickListener(this)
        }

        fun bind(item: FavouriteItem) {
            query = item.query
            queryTextView.text = query
            timestampTextView.text = formatDate(item.timestamp)
        }

        override fun onClick(v: View?) {
            // Search on Google when the query is clicked
            val searchUrl = "https://www.google.com/search?q=${Uri.encode(query)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
            try {
                v?.context?.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle if no activity is found to handle the intent
                e.printStackTrace()
            }
        }

        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
