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

    private var deadlineText: String? = null
    private val calendar: Calendar = Calendar.getInstance()

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

        saveButton.setOnClickListener {
            saveTask()
        }
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
            done = false
        )

        TaskRepository.addTask(task)
        finish()
    }
}