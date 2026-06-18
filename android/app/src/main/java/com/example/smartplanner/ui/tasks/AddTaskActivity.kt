package com.example.smartplanner.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.smartplanner.R
import com.example.smartplanner.data.TaskRepository
import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import com.example.smartplanner.model.Subtask
import java.util.Calendar
import java.util.Locale

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var priorityGroup: RadioGroup
    private lateinit var flagSwitch: Switch
    private lateinit var deadlineSwitch: Switch
    private lateinit var deadlineButton: Button
    private lateinit var saveButton: Button
    private lateinit var subtaskEdit: EditText
    private lateinit var subtaskAddButton: Button
    private lateinit var subtaskList: LinearLayout

    private var deadlineText: String? = null
    private val calendar: Calendar = Calendar.getInstance()
    private val subtasks = mutableListOf<Subtask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleEdit = findViewById(R.id.editTitle)
        descriptionEdit = findViewById(R.id.editDescription)
        priorityGroup = findViewById(R.id.priorityGroup)
        flagSwitch = findViewById(R.id.switchFlag)
        deadlineSwitch = findViewById(R.id.switchDeadline)
        deadlineButton = findViewById(R.id.buttonDeadline)
        saveButton = findViewById(R.id.buttonSave)
        subtaskEdit = findViewById(R.id.editSubtask)
        subtaskAddButton = findViewById(R.id.buttonAddSubtask)
        subtaskList = findViewById(R.id.layoutSubtasks)

        deadlineButton.isEnabled = false

        deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
            deadlineButton.isEnabled = isChecked
            if (!isChecked) {
                deadlineText = null
                deadlineButton.text = getString(R.string.deadline_button_title)
            }
        }

        deadlineButton.setOnClickListener {
            showDateTimePickers()
        }

        subtaskAddButton.setOnClickListener {
            addSubtask()
        }

        saveButton.setOnClickListener {
            saveTask()
        }

        renderSubtasks()
    }

    private fun showDateTimePickers() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateDialog = DatePickerDialog(this, { _, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timeDialog = TimePickerDialog(this, { _, h, min ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, min)

                val text = String.format(
                    Locale.getDefault(),
                    "%02d.%02d %02d:%02d",
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
                deadlineText = text
                deadlineButton.text = text
            }, hour, minute, true)

            timeDialog.show()
        }, year, month, day)

        dateDialog.show()
    }

    private fun getSelectedPriority(): TaskPriority {
        return when (priorityGroup.checkedRadioButtonId) {
            R.id.radioHigh -> TaskPriority.HIGH
            R.id.radioLow -> TaskPriority.LOW
            else -> TaskPriority.MEDIUM
        }
    }

    private fun saveTask() {
        val title = titleEdit.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show()
            return
        }

        val description = descriptionEdit.text.toString().trim()
        val priority = getSelectedPriority()
        val flagged = flagSwitch.isChecked
        val deadline = if (deadlineSwitch.isChecked) deadlineText else null

        val task = Task(
            id = TaskRepository.getNewId(),
            title = title,
            description = if (description.isEmpty()) null else description,
            priority = priority,
            flagged = flagged,
            deadlineText = deadline,
            done = false,
            subtasks = subtasks.toList()
        )

        TaskRepository.addTask(task)
        finish()
    }

    private fun addSubtask() {
        val title = subtaskEdit.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название подзадачи", Toast.LENGTH_SHORT).show()
            return
        }

        subtasks.add(
            Subtask(
                id = TaskRepository.getNewSubtaskId(),
                title = title,
                done = false
            )
        )
        subtaskEdit.text.clear()
        renderSubtasks()
    }

    private fun renderSubtasks() {
        subtaskList.removeAllViews()

        if (subtasks.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "Подзадач пока нет"
            }
            subtaskList.addView(emptyView)
            return
        }

        subtasks.forEach { subtask ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            val titleView = TextView(this).apply {
                text = "• ${subtask.title}"
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val removeButton = ImageButton(this).apply {
                setImageResource(android.R.drawable.ic_menu_delete)
                contentDescription = "Удалить подзадачу"
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setOnClickListener {
                    subtasks.removeAll { it.id == subtask.id }
                    renderSubtasks()
                }
            }
            row.addView(titleView)
            row.addView(removeButton)
            subtaskList.addView(row)
        }
    }
}
