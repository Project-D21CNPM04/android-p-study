# 📱 P-Study: Android Study Assistant

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen" alt="Platform Android">
  <img src="https://img.shields.io/badge/Kotlin-1.9.0-blue" alt="Kotlin 1.9.0">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License MIT">
  <img src="https://img.shields.io/badge/Version-1.0.0-orange" alt="Version 1.0.0">
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/android.svg" width="100" height="100" alt="P-Study Logo">
</p>

P-Study is a comprehensive study assistant application for Android that helps students organize their study materials, create flashcards, quizzes, and mind maps from their notes, and optimize their learning experience using AI-powered features.

## 📋 Table of Contents

- [🌟 Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [📊 System Design](#-system-design)
- [📄 Code Structure](#-code-structure)
- [🚀 Installation](#-installation)
- [🎮 How to Use](#-how-to-use)
- [📱 Screens](#-screens)
- [🧩 Modules](#-modules)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

## 🌟 Features

- **🔐 User Authentication**: Secure sign-up and login with Firebase Auth
- **📝 Note Taking**: Create and manage study notes from various sources
  - Text input 
  - URL content extraction
  - PDF/document upload
  - Audio transcription
  - Image to text conversion
- **🧠 AI-Powered Learning Tools**:
  - Flashcard generation
  - Quiz creation
  - Mind map visualization
  - Summary generation
- **📂 Organization**: Folder system for organized study materials
- **🔔 Reminders**: Study session reminders with notifications
- **🌓 Dark Mode**: Support for light and dark themes
- **📊 Progress Tracking**: Monitor your study progress

## 🏗️ Architecture

P-Study follows the **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles:

```
┌─────────────────────────────────────────────────────────────┐
│                       Presentation Layer                     │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Activity  │◄──►│  ViewModel  │◄──►│   UI State      │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                        Domain Layer                          │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │ Use Cases   │◄──►│   Entities  │◄──►│  Repositories   │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                         Data Layer                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │ Repository  │◄──►│   Remote    │◄──►│     Local       │  │
│  │   Impl      │    │ Data Source │    │  Data Source    │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Injection

We use **Dagger Hilt** for dependency injection to ensure:
- Loose coupling between components
- Testability
- Scalability
- Maintainability

## 🛠️ Tech Stack

P-Study leverages modern Android development tools and libraries:

### 📱 Frontend
- **Kotlin** - Primary programming language
- **Jetpack Components**:
  - ViewModel - For managing UI-related data
  - Room - For local database storage
  - LiveData/Flow - For reactive data handling
  - ViewBinding - For type-safe view access
- **Material Design Components** - For consistent UI/UX
- **Coroutines & Flow** - For asynchronous operations

### 🔄 Backend Integration
- **Retrofit** - For RESTful API communication
- **Gson** - For JSON serialization/deserialization
- **OkHttp** - For HTTP client

### 🔐 Authentication
- **Firebase Auth** - For user authentication

### 📜 Other Libraries
- **Markwon** - For Markdown rendering
- **Lottie** - For animations

## 📊 System Design

### Data Flow Diagram

```
┌───────────┐    ┌───────────┐    ┌───────────┐    ┌───────────┐
│           │    │           │    │           │    │           │
│  UI Layer │───►│ ViewModel │───►│ Use Cases │───►│Repository │
│           │    │           │    │           │    │           │
└───────────┘    └───────────┘    └───────────┘    └───────────┘
       ▲                                                  │
       │                                                  │
       │                                                  ▼
       │                                           ┌───────────┐
       │                                           │           │
       └───────────────────────────────────────────┤  Sources  │
                                                   │           │
                                                   └───────────┘
```

### Component Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       P-Study App                            │
└─────────────────────────────────────────────────────────────┘
          │                              │
          ▼                              ▼
┌─────────────────┐              ┌─────────────────┐
│     :app        │◄────────────►│    :base        │
└─────────────────┘              └─────────────────┘
```

## 📄 Code Structure

```
android/
├── app/                  # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/pstudy/
│   │   │   │   ├── data/           # Data layer components
│   │   │   │   │   └── notification/
│   │   │   │   ├── di/             # Dependency injection modules
│   │   │   │   ├── ext/            # Kotlin extension functions
│   │   │   │   ├── permission/      # Permission handling
│   │   │   │   ├── view/           # UI components
│   │   │   │   │   ├── authentication/  # Login/signup
│   │   │   │   │   ├── folder/      # Folder management
│   │   │   │   │   ├── home/        # Home screen
│   │   │   │   │   ├── input/       # Note input
│   │   │   │   │   ├── result/      # Results display
│   │   │   │   │   └── settings/    # App settings
│   │   │   │   └── PStudyApp.kt     # Application class
│   │   │   ├── res/             # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/         # Instrumentation tests
│   │   └── test/                # Unit tests
│   ├── build.gradle.kts         # App module build script
│   └── proguard-rules.pro       # ProGuard rules
├── base/                        # Base module with shared components
│   ├── src/                     # Source files for base module
│   └── build.gradle.kts         # Base module build script
├── gradle/                      # Gradle configuration
├── build.gradle.kts             # Project-level build script
└── settings.gradle.kts          # Project settings
```

## 🚀 Installation

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK (API level 24+)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/android-p-study.git
   cd android-p-study
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and click "Open"

3. **Build the project**
   - Wait for Gradle sync to complete
   - Select "Build > Make Project" from the menu

4. **Run the app**
   - Connect an Android device or use an emulator
   - Select "Run > Run 'app'" from the menu

## 🎮 How to Use

### User Authentication
1. Launch the app
2. Sign up with email or sign in with existing credentials
3. Optionally use social login options

### Creating Study Materials
1. Navigate to the Input screen
2. Choose your input method (text, URL, file, audio, image)
3. Provide the content or upload your material
4. Tap "Process" to analyze and store your content

### Generating Learning Tools
1. Open any saved note
2. Select the tool you want to generate (flashcards, quiz, mind map, or summary)
3. The AI will process your content and generate the selected tool
4. Save or modify the generated content as needed

### Organizing Content
1. Create folders to organize related materials
2. Use the search function to find specific content
3. Set reminders for study sessions

## 📱 Screens

The app includes the following main screens:

- **Login/Signup**: User authentication
- **Home**: Dashboard with recent materials and quick access to features
- **Input**: Various methods to input study materials
- **Results**: Display of processed content
- **Folders**: Organization of study materials
- **Settings**: App preferences and user profile

## 🧩 Modules

### :app Module
Contains the main application code, including UI components, business logic, and data handling.

### :base Module
Contains shared components, utilities, base classes, and extensions that are used across the app.

## 🤝 Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

<p align="center">
  Made with ❤️ by the P-Study Team
</p>
