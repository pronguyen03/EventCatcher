package me.linhthengo.androiddddarchitechture.presentation.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_events.view.*
import me.linhthengo.androiddddarchitechture.R
import me.linhthengo.androiddddarchitechture.models.Event
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(var events: MutableList<Event>) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {

    fun updateEvents(newEvents: MutableList<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(event: Event?) {
            val startDate = event?.startDate
            itemView.tv_date.text = SimpleDateFormat("dd", Locale.UK).format(startDate)
            itemView.tv_month.text = SimpleDateFormat("MMM", Locale.UK).format(startDate)
            itemView.tv_name.text = event?.name
            itemView.tv_location.text = event?.location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_events, parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(events[position])
    }

    override fun getItemCount(): Int = events.size
}