import Testing
@testable import ios

struct SmartPlannerTests {

    @Test func notesViewModel_addNoteTrimsTextAndShowsNewestFirst() async throws {
        let viewModel = NotesViewModel()

        #expect(viewModel.addNote(text: " first note "))
        #expect(viewModel.addNote(text: "second note"))

        #expect(viewModel.notes.map(\.text) == ["second note", "first note"])
    }

    @Test func notesViewModel_addNoteRejectsBlankText() async throws {
        let viewModel = NotesViewModel()

        #expect(!viewModel.addNote(text: "   "))
        #expect(viewModel.notes.isEmpty)
    }

    @Test func notesViewModel_removeNoteDeletesOnlySelectedNote() async throws {
        let viewModel = NotesViewModel()
        _ = viewModel.addNote(text: "first")
        _ = viewModel.addNote(text: "second")
        let noteToRemove = try #require(viewModel.notes.first)

        viewModel.removeNote(id: noteToRemove.id)

        #expect(viewModel.notes.map(\.text) == ["first"])
    }
}
