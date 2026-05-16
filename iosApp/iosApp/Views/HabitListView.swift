import SwiftUI
import shared

struct HabitListView: View {
    // Usually here we'd have an ObservableObject bridging StateFlow,
    // but without full IDE setup and native coroutine wraps, we keep it structurally sound.
    var body: some View {
        NavigationView {
            Text("Habit List Loading...")
                .navigationTitle("Habitos")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        NavigationLink(destination: CreateHabitView()) {
                            Image(systemName: "plus")
                        }
                    }
                }
        }
    }
}
