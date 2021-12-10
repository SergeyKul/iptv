package com.sirko.iptv

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChannelsView(listener: (Int) -> Unit) {
    private val adapter = ChannelsAdapter(listener)
    private var recyclerView: RecyclerView? = null
    private var view: View? = null
    val setCurrent: (Int) -> Unit = { current -> adapter.current = current }
    val addItem: (M3UItem) -> Unit = { item -> adapter.addItem(item) }
    val clearAdapter: () -> Unit = { adapter.clear() }

    fun init(activity: Activity, context: Context) {
        recyclerView = activity.findViewById<RecyclerView>(R.id.channels_recycler_view)?.also {
            it.layoutManager = LinearLayoutManager(context)
            it.itemAnimator = null
            it.adapter = adapter
        }
        view = activity.findViewById(R.id.channels_view)
    }

    fun updateVisibility(count: Int, position: Int?) {
        view?.let { view ->
            val visibility = view.visibility
            view.visibility = if (count == MainActivity.CHANNELS_STATE) View.VISIBLE else View.GONE
            position?.let { position ->
                if (visibility != view.visibility) {
                    adapter.current = position
                    recyclerView?.scrollToPosition(position)
                }
            }
        }
    }
}