import SwiftUI

struct EditTaskView: View {
    @Environment(\.dismiss) private var dismiss

    @State private var title: String = ""
    @State private var description: String = ""
    @State private var priority: TaskPriority = .medium
    @State private var isFlagged: Bool = false
    @State private var hasDeadline: Bool = false
    @State private var deadline: Date = Date()
    @State private var newSubtaskTitle: String = ""
    @State private var subtasks: [SubtaskModel] = []

    let onSave: (TaskModel) -> Void

    private var isSaveDisabled: Bool {
        title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        NavigationView {
            Form {
                Section {
                    TextField("Название задачи *", text: $title)
                    TextField("описание", text: $description, axis: .vertical)
                }

                Section("Приоритет") {
                    PrioritySelectorView(selected: $priority)
                }

                Section("Подзадачи") {
                    HStack {
                        TextField("Новая подзадача", text: $newSubtaskTitle)
                        Button("Добавить") {
                            addSubtask()
                        }
                        .disabled(newSubtaskTitle.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                    }

                    if subtasks.isEmpty {
                        Text("Подзадач пока нет")
                            .foregroundColor(.gray)
                    } else {
                        ForEach(subtasks) { subtask in
                            HStack {
                                Image(systemName: "circle")
                                    .foregroundColor(.gray)
                                Text(subtask.title)
                                Spacer()
                                Button(role: .destructive) {
                                    removeSubtask(id: subtask.id)
                                } label: {
                                    Image(systemName: "trash")
                                }
                            }
                        }
                    }
                }

                Section {
                    Toggle(isOn: $isFlagged) {
                        HStack {
                            Text("Флаг")
                            Text("!")
                                .foregroundColor(.red)
                        }
                    }
                }

                Section {
                    Toggle(isOn: $hasDeadline) {
                        Text("Выполнить до")
                    }
                    if hasDeadline {
                        DatePicker(
                            "",
                            selection: $deadline,
                            displayedComponents: [.date, .hourAndMinute]
                        )
                        .datePickerStyle(.compact)
                    }
                }
            }
            .navigationTitle("Сохранить")
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Сохранить") {
                        let task = TaskModel(
                            title: title,
                            description: description,
                            priority: priority,
                            isFlagged: isFlagged,
                            hasDeadline: hasDeadline,
                            deadline: hasDeadline ? deadline : nil,
                            subtasks: subtasks
                        )
                        onSave(task)
                        dismiss()
                    }
                    .disabled(isSaveDisabled)
                }

                ToolbarItem(placement: .cancellationAction) {
                    Button("Закрыть") {
                        dismiss()
                    }
                }
            }
        }
    }

    private func addSubtask() {
        let trimmed = newSubtaskTitle.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        subtasks.append(SubtaskModel(title: trimmed))
        newSubtaskTitle = ""
    }

    private func removeSubtask(id: UUID) {
        subtasks.removeAll { $0.id == id }
    }
}
