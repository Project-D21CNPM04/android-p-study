# P-Study Android Application

An Android mobile application for studying and learning, built using Kotlin and modern Android development practices. The application integrates with Firebase for authentication and uses a modular architecture for easy maintenance and extension.

## Table of Contents
- [System Overview](#system-overview)
- [Key Features](#key-features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Installation and Setup](#installation-and-setup)
- [Development Guide](#development-guide)
- [API Integration](#api-integration)
- [Firebase Integration](#firebase-integration)
- [Dependency Injection](#dependency-injection)
- [Data Flow](#data-flow)
- [Testing](#testing)
- [Contributing](#contributing)

## System Overview

P-Study is a comprehensive mobile learning platform designed to provide users with a structured and interactive studying experience. The application offers various features to enhance the learning process, including:

- **User Authentication**: Secure sign-up and login functionality using Firebase Authentication
- **Home Dashboard**: Personalized dashboard showcasing study materials and progress
- **Input Functionality**: Interface for users to input their study data and preferences
- **Results Visualization**: Clear presentation of study outcomes and analytics
- **Permission Management**: Proper handling of Android runtime permissions
- **Network Communication**: API integration for fetching and submitting study data

The system is designed with a modular architecture that separates concerns and ensures maintainability. It follows modern Android development practices and design patterns.

## Key Features

### User Authentication
- **Login**: User authentication with email and password
- **Sign Up**: New user registration
- **Password Reset**: Recovery options for forgotten passwords
- **User Profile**: Management of user information and preferences

### Home Interface
- **Dashboard**: Overview of study activities and progress
- **Navigation**: Intuitive movement between application features
- **Personalization**: Customized content based on user preferences

### Input Management
- **Data Entry**: Forms and interfaces for inputting study information
- **Validation**: Input checking to ensure data quality
- **Media Integration**: Support for various input types (text, images, etc.)

### Results and Analysis
- **Data Visualization**: Graphical representation of study outcomes
- **Progress Tracking**: Monitoring of learning achievements
- **Performance Metrics**: Quantitative measures of study effectiveness

### Application Settings
- **Preference Management**: User-configurable application settings
- **Theme Options**: Visual customization of the application
- **Notification Controls**: Management of app notifications

## Technologies Used

### Core Technologies
- **Kotlin**: Primary programming language
- **Android SDK**: Platform-specific development tools
- **Gradle KTS**: Build system with Kotlin DSL

### Architecture Components
- **View Binding**: Type-safe interaction with UI elements
- **Lifecycle Components**: Management of UI-related data
- **Navigation Component**: Handling of in-app navigation
- **Fragment Manager**: UI composition and management

### Networking
- **Retrofit**: Type-safe HTTP client for API communication
- **OkHttp**: HTTP client for network requests
- **Gson**: JSON serialization/deserialization

### Database and Authentication
- **Firebase Authentication**: User authentication services
- **Firebase Firestore**: Cloud-based NoSQL database (if used)
- **Firebase Storage**: Media storage solution (if used)

### Dependency Injection
- **Dagger Hilt**: Simplified dependency injection framework

### Testing
- **JUnit**: Unit testing framework
- **Espresso**: UI testing framework

## Project Structure

The project follows a modular architecture with clear separation of concerns:

```
android/
├── app/                    # Main application module
│   ├── src/                # Source code
│   │   ├── main/           # Main source set
│   │   │   ├── java/       # Kotlin/Java code
│   │   │   │   └── com/
│   │   │   │       └── example/
│   │   │   │           └── pstudy/
│   │   │   │               ├── data/        # Data handling
│   │   │   │               │   ├── firebase/  # Firebase services
│   │   │   │               │   ├── model/     # Data models
│   │   │   │               │   └── remote/    # Remote API services
│   │   │   │               ├── di/          # Dependency injection
│   │   │   │               ├── permission/  # Permission handling
│   │   │   │               └── view/        # UI components
│   │   │   │                   ├── authentication/  # Auth screens
│   │   │   │                   ├── home/            # Home screens
│   │   │   │                   ├── input/           # Input screens
│   │   │   │                   └── result/          # Result screens
│   │   │   ├── res/        # Resources
│   │   │   └── AndroidManifest.xml  # App manifest
│   │   ├── test/           # Unit tests
│   │   └── androidTest/    # Instrumentation tests
│   ├── build.gradle.kts    # App module build script
│   └── proguard-rules.pro  # ProGuard rules
├── base/                   # Base module with shared functionality
│   ├── src/                # Source code
│   └── build.gradle.kts    # Base module build script
├── gradle/                 # Gradle wrapper and plugins
├── build.gradle.kts        # Project build script
├── settings.gradle.kts     # Project settings
└── gradle.properties       # Gradle properties
```

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern with clean architecture principles:

```
┌───────────────────────────────────────────────────────────────┐
│                        Presentation Layer                      │
│                                                               │
│  ┌──────────┐       ┌─────────────┐       ┌───────────────┐   │
│  │  Views   │◄─────►│ ViewModels  │◄─────►│   Use Cases   │   │
│  └──────────┘       └─────────────┘       └───────────────┘   │
└───────────────────────────────────────────────────────────────┘
                                  ▲
                                  │
                                  ▼
┌───────────────────────────────────────────────────────────────┐
│                         Domain Layer                          │
│                                                               │
│                      ┌─────────────────┐                      │
│                      │     Models      │                      │
│                      └─────────────────┘                      │
└───────────────────────────────────────────────────────────────┘
                                  ▲
                                  │
                                  ▼
┌───────────────────────────────────────────────────────────────┐
│                          Data Layer                           │
│                                                               │
│  ┌──────────┐        ┌─────────────┐      ┌───────────────┐   │
│  │ Remote   │        │ Repositories │      │    Local      │   │
│  │   API    │◄─────► │              │◄────►│  Database     │   │
│  └──────────┘        └─────────────┘      └───────────────┘   │
└───────────────────────────────────────────────────────────────┘
```

### Component Interaction Flow

1. **User Interaction**: User interacts with UI elements in Activity or Fragment
2. **View Communication**: View sends events to ViewModel
3. **Data Processing**: ViewModel processes data through Use Cases
4. **Repository Access**: Use Cases access repositories to fetch or store data
5. **Data Sources**: Repositories communicate with Remote API or Local Database
6. **UI Update**: ViewModel updates LiveData, which is observed by Views
7. **User Feedback**: Updated UI is presented to the user

## Installation and Setup

### System Requirements
- Android Studio (latest stable version)
- JDK 17 or higher
- Android SDK with minimum API level 24 (Android 7.0)
- Gradle 8.0 or higher

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd android-p-study/android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Sync Gradle**
   - Android Studio should automatically sync the Gradle files
   - If not, click "Sync Project with Gradle Files" in the toolbar

4. **Configure Firebase (if needed)**
   - Create a Firebase project at [firebase.google.com](https://firebase.google.com)
   - Add an Android app to your Firebase project
   - Download the `google-services.json` file
   - Place it in the app directory

5. **Build the Project**
   ```bash
   ./gradlew build
   ```

6. **Run the Application**
   - Connect an Android device or use an emulator
   - Click the "Run" button in Android Studio

## Development Guide

### Coding Standards

The project follows the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with some additional rules:

- Use meaningful variable and function names
- Keep functions small and focused on a single responsibility
- Write comprehensive comments for complex logic
- Use extension functions for utility methods
- Follow MVVM architecture principles

### Adding New Features

1. **Create Feature Package**:
   - Add a new package under `com.example.pstudy.view` for UI components
   - Add corresponding packages for ViewModels, repositories, etc.

2. **Define Models**:
   - Create data models in `com.example.pstudy.data.model`

3. **Implement Repository**:
   - Create repository interface and implementation
   - Add necessary API service methods

4. **Create ViewModel**:
   - Implement ViewModel with appropriate LiveData or StateFlow objects
   - Add use cases for business logic

5. **Build UI Components**:
   - Create Activities/Fragments with layouts
   - Implement view binding
   - Set up observers for LiveData

6. **Update Navigation**:
   - Add new destinations to the navigation graph

### Working with Base Module

The `base` module contains common functionality and utilities:
- Reusable UI components
- Base classes for Activities and Fragments
- Extension functions
- Common utilities

When adding functionality that could be reused across features, consider adding it to the base module.

## API Integration

The application uses Retrofit for API communication:

```kotlin
interface StudyService {
    @GET("studies")
    suspend fun getStudies(): Response<List<Study>>

    @POST("studies")
    suspend fun createStudy(@Body study: Study): Response<Study>

    // Other API endpoints
}
```

API base URL and timeout configurations are defined in `NetworkConfig`:

```kotlin
object NetworkConfig {
    const val BASE_URL = "https://api.example.com/"
    const val TIME_OUT = 30L
}
```

## Firebase Integration

The application integrates with Firebase for authentication:

```kotlin
// Example of Firebase Authentication usage
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // User created successfully
        } else {
            // Handle error
        }
    }
```

## Dependency Injection

Dagger Hilt is used for dependency injection:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Implementation
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Implementation
    }

    @Provides
    @Singleton
    fun provideStudyService(retrofit: Retrofit): StudyService {
        // Implementation
    }
}
```

## Data Flow

The data flow in the application follows this pattern:

```
┌──────────┐    ┌──────────────┐    ┌────────────┐    ┌───────────┐
│   View   │───►│  ViewModel   │───►│ Repository │───►│ Data      │
│          │◄───│              │◄───│            │◄───│ Source    │
└──────────┘    └──────────────┘    └────────────┘    └───────────┘
```

1. View calls methods on ViewModel
2. ViewModel executes business logic and calls Repository
3. Repository decides whether to fetch from network or local database
4. Repository returns data to ViewModel, which updates LiveData
5. View observes LiveData changes and updates UI

## Testing

### Unit Testing

Unit tests are written for ViewModels, Repositories, and Use Cases:

```kotlin
@Test
fun `test study retrieval success`() = runBlockingTest {
    // Arrange
    val studies = listOf(Study(id = "1", title = "Test Study"))
    coEvery { studyRepository.getStudies() } returns Result.success(studies)
    
    // Act
    val result = getStudiesUseCase()
    
    // Assert
    assertTrue(result.isSuccess)
    assertEquals(studies, result.getOrNull())
}
```

### UI Testing

UI tests use Espresso to verify UI behavior:

```kotlin
@Test
fun loginButton_performsLogin_whenCredentialsAreValid() {
    // Arrange
    onView(withId(R.id.email_input)).perform(typeText("test@example.com"))
    onView(withId(R.id.password_input)).perform(typeText("password123"))
    
    // Act
    onView(withId(R.id.login_button)).perform(click())
    
    // Assert
    onView(withId(R.id.home_container)).check(matches(isDisplayed()))
}
```

## Contributing

Contributions to the P-Study Android application are welcome. Here's how to contribute:

1. **Fork the repository**
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit your changes**:
   ```bash
   git commit -m "Add your feature description"
   ```
4. **Push to your branch**:
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Create a Pull Request**

Please ensure your code follows the project's coding standards and includes appropriate tests.