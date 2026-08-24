import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinHelper().doInit()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
