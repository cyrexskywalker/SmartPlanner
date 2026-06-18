import Foundation

final class NotesViewModel: ObservableObject {
    @Published private(set) var notes: [NoteModel] = []

    func addNote(text: String) -> Bool {
        let trimmedText = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedText.isEmpty else { return false }

        notes.insert(NoteModel(text: trimmedText), at: 0)
        return true
    }

    func removeNote(id: UUID) {
        notes.removeAll { $0.id == id }
    }
}
