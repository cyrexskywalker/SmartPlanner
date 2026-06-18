import SwiftUI

struct ContentView: View {
    @StateObject private var tasksViewModel = TasksViewModel()
    @StateObject private var notesViewModel = NotesViewModel()

    var body: some View {
        TabView {
            NewsScreen()
                .tabItem {
                    Image(systemName: "newspaper")
                    Text("Новости")
                }

            TasksScreen()
                .environmentObject(tasksViewModel)
                .tabItem {
                    Image(systemName: "checklist")
                    Text("Задачи")
                }

            NotesScreen()
                .environmentObject(notesViewModel)
                .tabItem {
                    Image(systemName: "square.and.pencil")
                    Text("Записи")
                }
        }
    }
}
