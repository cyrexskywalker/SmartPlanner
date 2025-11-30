import SwiftUI

struct TasksScreen: View {
    @EnvironmentObject var viewModel: TasksViewModel

    private let dateFormatter: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "dd.MM HH:mm"
        return f
    }()

    @State private var showCreate: Bool = false

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.tasks.isEmpty {
                    Text("Задач нет")
                        .foregroundColor(.gray)
                        .padding(.top, 40)
                    Spacer()
                } else {
                    HStack {
                        Text("Сортировка по приоритету")
                            .font(.subheadline)
                        Spacer()
                        Toggle(isOn: $viewModel.highFirst) {
                            Text(viewModel.highFirst ? "Высокие ↑" : "Низкие ↑")
                                .font(.subheadline)
                        }
                        .toggleStyle(SwitchToggleStyle(tint: .blue))
                    }
                    .padding(.horizontal)
                    .padding(.top, 8)

                    ScrollView {
                        VStack(spacing: 16) {
                            ForEach(viewModel.groupedTasks) { group in
                                VStack(alignment: .leading, spacing: 8) {
                                    Text(group.priority.title)
                                        .font(.headline)
                                        .padding(.horizontal)

                                    ForEach(group.tasks) { task in
                                        TaskRowView(
                                            task: task,
                                            dateFormatter: dateFormatter,
                                            onToggleDone: {
                                                viewModel.toggleDone(id: task.id)
                                            }
                                        )
                                        .padding(.horizontal)
                                    }
                                }
                            }
                        }
                        .padding(.top, 8)
                    }
                }
            }
            .navigationTitle("Задачи")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        showCreate = true
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showCreate) {
                EditTaskView { task in
                    viewModel.add(task: task)
                }
            }
        }
    }
}
