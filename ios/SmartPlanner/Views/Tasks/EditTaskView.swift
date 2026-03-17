import SwiftUI

struct EditTaskView: View {
    @Environment(\.dismiss) private var dismiss

    @State private var title: String = ""
    @State private var description: String = ""
    @State private var priority: TaskPriority = .medium
    @State private var isFlagged: Bool = false
    @State private var hasDeadline: Bool = false
    @State private var deadline: Date = Date()

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
                            deadline: hasDeadline ? deadline : nil
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
}
