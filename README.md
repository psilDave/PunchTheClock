# PunchTheClock ⏰

A modern time-tracking and clock-in application developed for Android and Wear OS.

## 🚀 The Project

**PunchTheClock** was designed to simplify work shift logs, offering a seamless experience between the smartphone and the smartwatch.

### Architecture & Modules

The project follows a multi-module architecture to promote code reuse and separation of concerns:

-   **`:mobile`**: Main Android application built with Jetpack Compose.
-   **`:wear`**: Specific application for Wear OS, allowing quick check-ins directly from the wrist.
-   **`:shared`**: Library module containing business logic, data models, and database configurations shared across platforms.

## 🛠 Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Mobile & Wear)
-   **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
-   **Database**: [Room](https://developer.android.com/training/data-storage/room)
-   **CI/CD**: GitHub Actions (Automated Build & Tests)

---
**🌐 Portuguese version:** [README.pt-br.md](./README.pt-br.md)