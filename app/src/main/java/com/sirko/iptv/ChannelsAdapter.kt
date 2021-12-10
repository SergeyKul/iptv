package com.sirko.iptv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ChannelsAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<ChannelsAdapter.ViewHolder>() {
    private val items = ArrayList<M3UItem>()
    private val color = -608
    var current = 0
        set(value) {
            notifyItemChanged(field)
            notifyItemChanged(value)
            field = value
        }

    fun addItem(item: M3UItem) {
        items.add(item)
        notifyItemChanged(items.size - 1)
    }

    fun clear() = items.clear()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.channels_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { m3u ->
            holder.itemView.apply {
                setOnClickListener { listener(position) }
                setOnFocusChangeListener { _: View, focused: Boolean ->
                    val color = if (focused) color else Color.WHITE
                    holder.cardView.setBackgroundColor(color)
                }
                holder.textView.text = m3u.name
                if (m3u.icon != "") Picasso.get().load(m3u.icon).into(holder.imageView)
                if (position == current) {
                    isFocusableInTouchMode = true
                    requestFocus()
                    isFocusableInTouchMode = false
                }
            }
        }
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_view)
        var textView: TextView = itemView.findViewById(R.id.text_view)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
    }
}