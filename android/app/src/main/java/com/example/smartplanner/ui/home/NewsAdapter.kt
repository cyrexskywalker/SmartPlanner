package com.example.smartplanner.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.model.NewsArticle
import java.util.concurrent.Executors

class NewsAdapter(
    private var items: List<NewsArticle>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    companion object {
        private val imageExecutor = Executors.newFixedThreadPool(4)
    }

    fun submitList(newItems: List<NewsArticle>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.imagePreview)
        private val imagePlaceholder: TextView = view.findViewById(R.id.textImagePlaceholder)
        private val imageLoader: ProgressBar = view.findViewById(R.id.progressImage)
        private val title: TextView = view.findViewById(R.id.textNewsTitle)
        private val abstractText: TextView = view.findViewById(R.id.textNewsAbstract)
        private val meta: TextView = view.findViewById(R.id.textNewsMeta)

        fun bind(item: NewsArticle) {
            title.text = item.title
            abstractText.text = item.abstractText
            meta.text = "${item.source} · ${item.section} · ${item.publishedDate}"

            image.setImageDrawable(null)
            image.tag = item.imageUrl

            if (item.imageUrl.isNullOrBlank()) {
                imageLoader.visibility = View.GONE
                imagePlaceholder.visibility = View.VISIBLE
                imagePlaceholder.text = "Изображение недоступно"
                return
            }

            imageLoader.visibility = View.VISIBLE
            imagePlaceholder.visibility = View.VISIBLE
            imagePlaceholder.text = "Загрузка изображения"

            imageExecutor.execute {
                val bitmap = NewsImageCache.load(item.imageUrl, itemView.context.cacheDir)

                image.post {
                    if (image.tag != item.imageUrl) return@post
                    imageLoader.visibility = View.GONE
                    if (bitmap != null) {
                        image.setImageBitmap(bitmap)
                        imagePlaceholder.visibility = View.GONE
                    } else {
                        imagePlaceholder.visibility = View.VISIBLE
                        imagePlaceholder.text = "Изображение недоступно"
                    }
                }
            }
        }
    }
}
