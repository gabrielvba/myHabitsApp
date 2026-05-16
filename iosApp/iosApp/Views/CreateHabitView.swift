import SwiftUI
import shared

struct CreateHabitView: View {
    @State private var name: String = ""
    @State private var emoji: String = "📝"
    @State private var isWeekly: Bool = false
    @State private var timesPerWeek: Float = 1.0

    var body: some View {
        Form {
            Section(header: Text("Details")) {
                TextField("Nome", text: $name)
                TextField("Emoji", text: $emoji)
            }

            Section(header: Text("Frequency")) {
                Toggle("Is Weekly?", isOn: $isWeekly)

                if isWeekly {
                    VStack {
                        Text("Times per week: \(Int(timesPerWeek))")
                        Slider(value: $timesPerWeek, in: 1...7, step: 1)
                    }
                }
            }

            Button("Save") {
                // let usecase = CreateHabitUseCase(...)
                // usecase.invoke(...)
            }
        }
        .navigationTitle("New Habit")
    }
}
