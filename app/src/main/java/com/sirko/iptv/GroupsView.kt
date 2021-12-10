package com.sirko.iptv

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupsView(listener: (Int) -> Unit) {
    private val adapter = GroupsAdapter(listener)
    private var recyclerView: RecyclerView? = null
    val setCurrent: (Int) -> Unit = { current -> adapter.current = current }
    val addItem: (String) -> Unit = { item -> adapter.addItem(item) }
    val current: Int get() = adapter.current
    val title: String get() = adapter.title

    fun init(activity: Activity, context: Context) {
        recyclerView = activity.findViewById<RecyclerView>(R.id.groups_recycler_view)?.also {
            it.layoutManager = LinearLayoutManager(context)
            it.itemAnimator = null
            it.adapter = adapter
        }
        addItem(context.getString(R.string.all_сhannels))
    }

    fun updateVisibility(count: Int) {
        recyclerView?.visibility =
            if (count == MainActivity.GROUPS_STATE) View.VISIBLE else View.GONE
        adapter.update()
    }
}