import SwiftUI

struct PrioritySelectorView: View {
    @Binding var selected: TaskPriority

    var body: some View {
        HStack(spacing: 16) {
            ForEach(TaskPriority.allCases) { priority in
                Circle()
                    .fill(priority.color.opacity(selected == priority ? 1 : 0.3))
                    .frame(width: 22, height: 22)
                    .onTapGesture {
                        selected = priority
                    }
            }
            Spacer()
        }
    }
}
