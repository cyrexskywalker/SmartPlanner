package com.example.smartplanner.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.data.TaskRepository
import com.example.smartplanner.model.TaskGroup

class TaskListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var addButton: ImageButton
    private lateinit var sortCheckBox: CheckBox
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerTasks)
        emptyText = view.findViewById(R.id.textEmpty)
        addButton = view.findViewById(R.id.buttonAdd)
        sortCheckBox = view.findViewById(R.id.checkSortHighFirst)

        adapter = TaskAdapter(
            items = groupedItems(),
            onTaskDoneChanged = { taskId ->
                TaskRepository.toggleDone(taskId)
                reloadList()
            },
            onSubtaskDoneChanged = { taskId, subtaskId ->
                TaskRepository.toggleSubtask(taskId, subtaskId)
                reloadList()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }

        sortCheckBox.isChecked = TaskRepository.highFirst
        sortCheckBox.setOnCheckedChangeListener { _, isChecked ->
            TaskRepository.highFirst = isChecked
            reloadList()
        }

        updateEmpty()
    }

    override fun onResume() {
        super.onResume()
        reloadList()
    }

    private fun reloadList() {
        adapter.submitList(groupedItems())
        updateEmpty()
    }

    private fun updateEmpty() {
        emptyText.visibility = if (TaskRepository.hasTasks()) View.GONE else View.VISIBLE
    }

    private fun groupedItems(): List<TaskListItem> {
        return groupsToListItems(TaskRepository.getGroups())
    }

    private fun groupsToListItems(groups: List<TaskGroup>): List<TaskListItem> {
        return buildList {
            groups.forEach { group ->
                add(TaskListItem.Header(group.priority))
                group.tasks.forEach { task ->
                    add(TaskListItem.Row(task))
                }
            }
        }
    }
}
