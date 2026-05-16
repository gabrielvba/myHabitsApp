import SwiftUI
import shared

struct HabitDetailView: View {
    let habitId: String

    var body: some View {
        VStack {
            Text("Detalhes do hábito \(habitId)")
                .font(.headline)

            // Grid de mock para os dias
            let columns = Array(repeating: GridItem(.flexible()), count: 7)
            LazyVGrid(columns: columns, spacing: 10) {
                ForEach(1...31, id: \.self) { day in
                    Text("\(day)")
                        .frame(width: 30, height: 30)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(15)
                }
            }
            .padding()

            Spacer()
        }
        .navigationTitle("Detalhes")
    }
}
