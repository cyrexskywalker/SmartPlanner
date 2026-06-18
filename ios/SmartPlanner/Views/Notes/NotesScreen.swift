import SwiftUI

struct NotesScreen: View {
    @EnvironmentObject private var viewModel: NotesViewModel
    @State private var noteText = ""
    @State private var showsEmptyInputAlert = false

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 16) {
                TextEditor(text: $noteText)
                    .frame(minHeight: 110)
                    .padding(10)
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .stroke(Color.secondary.opacity(0.25))
                    )
                    .overlay(alignment: .topLeading) {
                        if noteText.isEmpty {
                            Text("Текст заметки")
                                .foregroundStyle(.secondary)
                                .padding(.horizontal, 16)
                                .padding(.vertical, 18)
                                .allowsHitTesting(false)
                        }
                    }

                Button {
                    addNote()
                } label: {
                    Text("Добавить заметку")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)

                if viewModel.notes.isEmpty {
                    Text("Заметок пока нет")
                        .foregroundStyle(.secondary)
                        .padding(.top, 8)
                } else {
                    List {
                        ForEach(viewModel.notes) { note in
                            Text(note.text)
                                .padding(.vertical, 6)
                        }
                        .onDelete { indexSet in
                            indexSet
                                .map { viewModel.notes[$0].id }
                                .forEach { viewModel.removeNote(id: $0) }
                        }
                    }
                    .listStyle(.plain)
                }

                Spacer(minLength: 0)
            }
            .padding()
            .navigationTitle("Записи")
            .alert("Введите текст заметки", isPresented: $showsEmptyInputAlert) {
                Button("ОК", role: .cancel) {}
            }
        }
    }

    private func addNote() {
        if viewModel.addNote(text: noteText) {
            noteText = ""
        } else {
            showsEmptyInputAlert = true
        }
    }
}
