package com.learning.alarmapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.alarmapp.databinding.ItemAlarmBinding
import java.text.SimpleDateFormat
import java.util.*

class AlarmAdapter(
    private val alarms: List<Alarm>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount() = alarms.size

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            binding.alarmTimeTextView.text = timeFormat.format(Date(alarm.time))
            binding.deleteAlarmButton.setOnClickListener { onDeleteClick(adapterPosition) }
        }
    }
}
// Explanation of each function and the inner class in the AlarmAdapter class:
// - onCreateViewHolder: This function is called when the RecyclerView needs a new ViewHolder to represent an item.
//   It inflates the layout for the item view using the ItemAlarmBinding class.