package com.example.smartplanner.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.model.TaskPriority

class TaskAdapter(
    private var items: List<TaskListItem>,
    private val onTaskDoneChanged: (Long) -> Unit,
    private val onSubtaskDoneChanged: (Long, Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ROW = 1
    }

    fun submitList(newItems: List<TaskListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TaskListItem.Header -> TYPE_HEADER
            is TaskListItem.Row -> TYPE_ROW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_task_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_task, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when {
            holder is HeaderViewHolder && item is TaskListItem.Header -> holder.bind(item)
            holder is TaskViewHolder && item is TaskListItem.Row -> holder.bind(item)
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.headerTitle)
        fun bind(item: TaskListItem.Header) {
            title.text = item.priority.title
        }
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val doneBox: CheckBox = view.findViewById(R.id.checkDone)
        private val title: TextView = view.findViewById(R.id.textTitle)
        private val description: TextView = view.findViewById(R.id.textDescription)
        private val deadline: TextView = view.findViewById(R.id.textDeadline)
        private val flagIcon: ImageView = view.findViewById(R.id.imageFlag)
        private val dotLow: View = view.findViewById(R.id.dotLow)
        private val dotMed: View = view.findViewById(R.id.dotMed)
        private val dotHigh: View = view.findViewById(R.id.dotHigh)
        private val subtasksContainer: LinearLayout = view.findViewById(R.id.layoutSubtasks)

        fun bind(item: TaskListItem.Row) {
            val task = item.task
            title.text = task.title
            setStrikeThrough(title, task.done)

            if (task.description.isNullOrEmpty()) {
                description.visibility = View.GONE
            } else {
                description.visibility = View.VISIBLE
                description.text = task.description
                setStrikeThrough(description, task.done)
            }

            if (task.deadlineText == null) {
                deadline.visibility = View.GONE
            } else {
                deadline.visibility = View.VISIBLE
                deadline.text = task.deadlineText
            }

            flagIcon.visibility = if (task.flagged) View.VISIBLE else View.INVISIBLE

            val ctx = itemView.context
            val gray = ctx.getColor(R.color.dotDisabled)
            val red = ctx.getColor(R.color.dotRed)
            val yellow = ctx.getColor(R.color.dotYellow)
            val green = ctx.getColor(R.color.dotGreen)

            fun setDotColors(active: TaskPriority) {
                dotLow.setBackgroundColor(if (active == TaskPriority.LOW) green else gray)
                dotMed.setBackgroundColor(if (active == TaskPriority.MEDIUM) yellow else gray)
                dotHigh.setBackgroundColor(if (active == TaskPriority.HIGH) red else gray)
            }

            setDotColors(task.priority)

            subtasksContainer.removeAllViews()
            if (task.subtasks.isEmpty()) {
                subtasksContainer.visibility = View.GONE
            } else {
                subtasksContainer.visibility = View.VISIBLE
                task.subtasks.forEach { subtask ->
                    val row = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.item_subtask, subtasksContainer, false)
                    val subtaskCheck = row.findViewById<CheckBox>(R.id.checkSubtaskDone)
                    val subtaskTitle = row.findViewById<TextView>(R.id.textSubtaskTitle)
                    subtaskTitle.text = subtask.title
                    setStrikeThrough(subtaskTitle, subtask.done)
                    subtaskCheck.setOnCheckedChangeListener(null)
                    subtaskCheck.isChecked = subtask.done
                    subtaskCheck.setOnCheckedChangeListener { _, isChecked ->
                        setStrikeThrough(subtaskTitle, isChecked)
                        onSubtaskDoneChanged(task.id, subtask.id)
                    }
                    subtasksContainer.addView(row)
                }
            }

            doneBox.setOnCheckedChangeListener(null)
            doneBox.isChecked = task.done
            doneBox.setOnCheckedChangeListener { _, isChecked ->
                setStrikeThrough(title, isChecked)
                if (description.visibility == View.VISIBLE) {
                    setStrikeThrough(description, isChecked)
                }
                onTaskDoneChanged(task.id)
            }
        }

        private fun setStrikeThrough(textView: TextView, enabled: Boolean) {
            textView.paintFlags = if (enabled) {
                textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }
}
