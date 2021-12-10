package com.sirko.iptv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {
    private val groups = ArrayList<String>()
    private val color = -608
    var current = 0
        set(value) {
            notifyItemChanged(field)
            notifyItemChanged(value)
            field = value
        }
    val title: String get() = groups[current]

    fun addItem(group: String) {
        if (!groups.contains(group)) groups.add(group)
        notifyItemChanged(groups.size - 1)
    }

    fun update() = notifyItemChanged(current)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.groups_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.apply {
            setOnClickListener { listener(position) }
            setOnFocusChangeListener { _: View, focused: Boolean ->
                val color = if (focused) color else Color.WHITE
                holder.cardView.setBackgroundColor(color)
            }
            holder.textView.text = groups[position]
            if (position == current) {
                isFocusableInTouchMode = true
                requestFocus()
                isFocusableInTouchMode = false
            }
        }
    }

    override fun getItemCount() = groups.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text_view)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
    }
}