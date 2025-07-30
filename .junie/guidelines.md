# Lazuli Project Guidelines

## Project Overview

Lazuli is a modern Android list management application built with Jetpack Compose. The app allows users to create and manage lists with items, featuring OCR (Optical Character Recognition) capabilities through camera integration to automatically extract text from images and add it to lists.

### Key Features
- Create and manage multiple lists
- Add, edit, and delete items within lists
- Camera integration with ML Kit text recognition for OCR
- Material Design 3 UI with dynamic theming
- Dark/light theme support
- In-app updates functionality
- Local data persistence with Room database

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Camera**: CameraX
- **Text Recognition**: ML Kit
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle with Kotlin DSL
- **Code Quality**: KtLint for formatting

## Project Structure

```
app/src/main/java/com/softklass/lazuli/
├── MainActivity.kt                    # Main entry point with theme and update management
├── data/
│   ├── database/                      # Room database components
│   │   ├── ItemDao.kt                # Data access for items
│   │   ├── ParentDao.kt              # Data access for lists/parents
│   │   └── ListDatabase.kt           # Database configuration
│   ├── models/                       # Data models
│   │   ├── Item.kt                   # Item entity
│   │   ├── Parent.kt                 # List/Parent entity
│   │   ├── ListItem.kt               # UI model
│   │   └── Resource.kt               # Resource wrapper
│   └── repository/                   # Repository layer
│       ├── ItemRepository.kt         # Item data operations
│       └── ParentRepository.kt       # Parent data operations
├── di/                               # Dependency injection
│   ├── App.kt                        # Application class
│   └── DatabaseModule.kt             # Database DI module
└── ui/                               # UI components
    ├── main/                         # Main screen (list of lists)
    ├── detail/                       # List detail screen (items in a list)
    ├── edit/                         # Item editing screen
    ├── camera/                       # Camera and OCR functionality
    ├── settings/                     # Settings screen
    ├── navigation/                   # Navigation setup
    ├── theme/                        # Theme and styling
    └── particles/                    # Reusable UI components
```

## Testing Guidelines

### Running Tests
- **Unit Tests**: `run_test app/src/test/java/com/softklass/lazuli/ExampleUnitTest.kt`
- **Instrumented Tests**: `run_test app/src/androidTest/java/com/softklass/lazuli/tests/MainTest.kt`
- **All Tests**: `run_test app/src/test;app/src/androidTest`

### Test Structure
- Unit tests are located in `app/src/test/`
- Instrumented (UI) tests are located in `app/src/androidTest/`
- Test orchestrator is enabled for better test isolation
- UI tests use Espresso and Compose testing framework

### When to Run Tests
- Always run tests after making changes to core functionality
- Run instrumented tests when modifying UI components
- Run unit tests when changing business logic or data operations
- Test camera and OCR functionality manually as it requires device hardware

## Build Instructions

### Building the Project
- **Debug Build**: The project builds automatically when running tests
- **Release Build**: Use `build` command only if specifically needed for verification
- **Code Formatting**: KtLint runs automatically before build (`preBuild` depends on `ktlintFormat`)

### Build Configuration
- **Target SDK**: 36 (Android 14)
- **Min SDK**: 26 (Android 8.0)
- **Java Version**: 21
- **Kotlin**: Latest stable version
- **Proguard**: Enabled for release builds

### Dependencies
The project uses version catalogs (`gradle/libs.versions.toml`) for dependency management.

## Code Style Guidelines

### Formatting
- **KtLint**: Automatically enforced with pre-build formatting
- **Max Line Length**: Disabled (no limit)
- **Composable Functions**: Exempt from standard function naming rules
- **Reporters**: Checkstyle, JSON, and HTML reports generated

### Architecture Patterns
- **MVVM**: ViewModels handle UI state and business logic
- **Repository Pattern**: Repositories abstract data access
- **Single Source of Truth**: Room database is the primary data source
- **Unidirectional Data Flow**: State flows down, events flow up

### Compose Guidelines
- Use `@Composable` functions for UI components
- Prefer stateless composables when possible
- Use `remember` and `rememberSaveable` appropriately
- Follow Material Design 3 principles
- Use proper test tags for UI testing

### Naming Conventions
- **Packages**: lowercase with dots (e.g., `com.softklass.lazuli.ui.main`)
- **Classes**: PascalCase (e.g., `MainActivity`, `MainViewModel`)
- **Functions**: camelCase (e.g., `onDetailItemClick`)
- **Variables**: camelCase (e.g., `listName`, `openDialog`)
- **Constants**: UPPER_SNAKE_CASE

### Database Guidelines
- Use Room entities with proper annotations
- Implement DAOs with suspend functions for async operations
- Use Flow for reactive data streams
- Handle database migrations properly

## Development Workflow

1. **Code Changes**: Make minimal, focused changes
2. **Formatting**: KtLint will auto-format on build
3. **Testing**: Run relevant tests to verify functionality
4. **Manual Testing**: Test camera/OCR features manually if modified
5. **Build Verification**: Only build if tests don't provide sufficient verification

## Special Considerations

- **Camera Permissions**: Required for OCR functionality
- **ML Kit**: Text recognition requires Google Play Services
- **In-App Updates**: Flexible update flow implemented
- **Theme Management**: Dynamic color and dark/light theme support
- **Edge-to-Edge**: Modern Android UI with proper insets handling
