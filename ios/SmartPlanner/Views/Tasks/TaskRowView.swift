import SwiftUI

struct TaskRowView: View {
    let task: TaskModel
    let dateFormatter: DateFormatter
    let onToggleDone: () -> Void

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Button(action: onToggleDone) {
                Circle()
                    .strokeBorder(Color.gray, lineWidth: 2)
                    .background(
                        Circle()
                            .fill(task.isDone ? Color.blue : Color.clear)
                    )
                    .frame(width: 22, height: 22)
            }

            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(task.title)
                        .font(.headline)
                    Spacer()
                    if task.isFlagged {
                        Text("!")
                            .foregroundColor(.red)
                            .font(.headline)
                    }
                }

                if !task.description.isEmpty {
                    Text(task.description)
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }

                HStack(spacing: 6) {
                    ForEach(TaskPriority.allCases) { p in
                        Circle()
                            .fill(p == task.priority ? p.color : Color.gray.opacity(0.2))
                            .frame(width: 10, height: 10)
                    }
                    Spacer()
                    if task.hasDeadline, let deadline = task.deadline {
                        Text(dateFormatter.string(from: deadline))
                            .font(.footnote)
                            .foregroundColor(.gray)
                    }
                }
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.white)
                .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 2)
        )
    }
}
