# Lazuli Engineering Guidelines

This document defines how we build, structure, and maintain the Lazuli Android app. It encodes Android best practices as of 2025-08-17 and should be followed for all code changes.

Goals:
- Ship maintainable, testable, and scalable features.
- Adhere to MVVM and Clean Architecture (data/domain/presentation).
- Apply DRY and SOLID principles with proven design patterns.
- Prefer composition over inheritance. Keep functions small and pure when possible.


## 1. Architecture Overview

We follow Clean Architecture with three layers and MVVM in the presentation layer.

- Presentation (UI + ViewModel)
  - Technologies: Jetpack Compose, Navigation Compose, Material 3
  - Responsibilities: UI rendering, user interactions, state management, calling use cases.
- Domain (Use Cases + Domain Models)
  - Technology: Pure Kotlin (no Android dependencies)
  - Responsibilities: Business rules, application-specific logic, interfaces for repositories.
- Data (Repositories + Data Sources)
  - Technologies: Room (SQLite), DataStore (if/when used), network clients (if added), CameraX/ML Kit wrappers
  - Responsibilities: Read/write from local DB, device capabilities, remote APIs; mapping to domain models.

Dependencies flow inward only:
Presentation → Domain → Data

Avoid crossing boundaries directly (e.g., UI should not depend on Room entities). Map between layers explicitly.


## 2. MVVM: Presentation Layer

- UI: Stateless composables where possible, driven by state from ViewModels.
- ViewModel: Exposes UI state as StateFlow (or Compose state) and UI events via functions. No Android context leaks.
- State:
  - Prefer a single immutable UI state data class per screen (copy to update).
  - Use a "Resource" or sealed result pattern for loading/success/error.
- Events: One-off events (navigation, toasts, snackbars) via SharedFlow/Channel or Compose’s effect handlers.
- Navigation: Use Navigation Compose with type-safe routes and argument objects where appropriate. Keep navigation decisions in the UI layer in response to ViewModel state or events.


## 3. Domain Layer

- Use Cases: One class per coherent business action (e.g., AddItem, GetItemsForList). Keep them small and pure.
- Interfaces: Define repository interfaces here that express the business needs, not the data storage details.
- Models: Domain models are UI-agnostic and storage-agnostic.
- Testing: Domain has the highest unit-test coverage; it’s pure Kotlin and fast to test.


## 4. Data Layer

- Repositories: Implement domain repository interfaces; orchestrate data sources (Room, ML Kit wrappers, etc.).
- Data Sources:
  - Local: Room DAOs for persistence. Expose suspend functions and Flows. No business logic in DAOs.
  - Remote: If added later, keep client code here. Handle retries, mapping, and error conversion.
  - Device: CameraX/ML Kit wrappers live here and expose clean interfaces to domain.
- Mappers: Provide clear mapping between Entity ↔ Domain ↔ UI models. Keep mappers in the layer that knows both types (e.g., data layer maps Entity↔Domain).


## 5. Principles and Patterns

- DRY: Extract common logic into reusable functions/components (e.g., debounce, dialog, app bar already present in ui/particles).
- SOLID:
  - Single Responsibility: Each class/composable does one thing well.
  - Open/Closed: Prefer extension and composition to modification.
  - Liskov: Respect substitutability in interfaces and implementations.
  - Interface Segregation: Prefer small, specific interfaces (e.g., repository contracts by feature).
  - Dependency Inversion: Higher-level modules depend on abstractions; use Hilt to inject implementations.
- Error Handling:
  - Convert exceptions to domain-understandable failures (sealed types or Resource.Error).
  - Centralize error mapping at layer boundaries.
  - Avoid catching Exception broadly unless translating to a result type.
- Concurrency:
  - Use Kotlin Coroutines and Flow. Offload IO work with Dispatchers.IO; don’t block the main thread.
  - Use viewModelScope for presentation-layer jobs.
  - Prefer cold Flows from repositories/DAOs; collect in ViewModels.
- Caching:
  - Room is the SSOT. Keep UI derived from database flows when possible.


## 6. Dependency Injection (Hilt)

- Provide database, DAOs, repositories, and other data services from Hilt modules in di/.
- Inject ViewModels using @HiltViewModel and constructor injection.
- Keep module bindings aligned with domain interfaces and data implementations.


## 7. Compose and UI Best Practices

- Stateless over stateful composables; pass state + event lambdas down.
- Remember/rememberSaveable only for UI state that must survive recomposition/process-death respectively.
- Use Material 3 components and theming (dynamic color where available).
- Accessibility: Provide content descriptions, semantics, and test tags.
- Performance: Avoid heavy work in composition. Use derivedStateOf for expensive computations based on state.
- Preview: Create meaningful @Preview where useful. Keep previews lightweight and without external IO.


## 8. Navigation

- Define routes in a single place (ui/navigation/Navigation.kt). Avoid stringly-typed routes scattered across code.
- Prefer typed arguments and NavType serializers if passing complex data. Otherwise pass IDs and load data within the destination screen.
- Navigation decisions are triggered by UI events; ViewModels expose intent (e.g., item selected), UI performs navigation.


## 9. Data Persistence (Room)

- DAOs use suspend functions and Flow where streaming is appropriate.
- Queries are small and explicit. Business rules do not belong in DAOs.
- Migrations must be provided when schema changes. Bump version and write tests where possible.
- Entities stay in data/models. Do not leak entities to UI; map to domain/UI models.


## 10. Camera and OCR

- CameraX and ML Kit integration is encapsulated behind interfaces implemented in the data/device layer.
- Handle permissions via a UI-friendly flow that surfaces states to ViewModel/UI; do not block the main thread.
- ML Kit processing should be cancellable, done off the main thread, and expose results as domain types or Resource.


## 11. Resource/Result Pattern

- Use a Resource (Success, Loading, Error) or sealed Result to communicate state across layers.
- UI renders based on this state and avoids throwing exceptions.


## 12. Testing Strategy

- Unit Tests (app/src/test):
  - Domain: exhaustive tests for use cases and mappers.
  - Data: repository behavior with fake data sources/DAOs.
  - ViewModel: business logic and state reduction tested via coroutine test APIs.
- Instrumented Tests (app/src/androidTest):
  - Compose UI tests with semantics and test tags.
  - Navigation flows and critical user journeys.
- When to run:
  - Run unit tests on business logic changes.
  - Run instrumented tests on UI/Compose changes.
- Mocks/Fakes: Prefer fakes for repositories/DAOs; use MockK for behavior verification when necessary.


## 13. Coding Conventions

- Kotlin style enforced by KtLint. No max line length limit.
- Naming:
  - Packages: lowercase, dot-separated.
  - Classes: PascalCase. Functions/vars: camelCase. Constants: UPPER_SNAKE_CASE.
  - Composable names may be PascalCase and are exempt from standard function naming rules.
- Nullability: Prefer non-null types; use nullable only when necessary and handle explicitly.
- Immutability: Use val by default; prefer immutable data classes for state.


## 14. Folder Structure (Current + Recommended)

- app/src/main/java/com/softklass/lazuli/
  - data/
    - database/ (Room DB, DAOs)
    - models/ (Entities, DTOs as needed)
    - repository/ (Repository implementations)
  - domain/ (Recommended to add)
    - model/ (Domain models)
    - repository/ (Repository interfaces)
    - usecase/ (Use case classes)
  - ui/
    - feature folders (main, detail, edit, camera, settings)
    - navigation/
    - particles/ (reusable UI components)
    - theme/
  - di/
- Keep layers separated with clear dependencies as described above.


## 15. Error Reporting and Logging

- Use structured logs for debug builds where necessary; avoid logging sensitive data.
- Surface user-friendly errors to UI; avoid leaking internal exception messages.
- Consider a central ErrorMapper in data or domain to convert throwables into domain failures.


## 16. In-App Updates and Permissions

- Updates: Use Play Core’s flexible updates with user-friendly prompts. Handle failures and resumption gracefully.
- Permissions: Request at point of use with clear rationale. Reflect permission state in ViewModel/UI.


## 17. Performance and Stability

- Avoid heavy work on main thread; use coroutines with proper dispatchers.
- Debounce/throttle user inputs where appropriate (see ui/particles/Debounce.kt).
- Use snapshotFlow or collectAsStateWithLifecycle to observe flows in Compose.


## 18. Contribution Checklist

Before submitting PRs:
- [ ] Architecture boundaries respected (no UI→Room direct dependency).
- [ ] New code follows DRY and SOLID.
- [ ] ViewModels expose immutable state and event handlers.
- [ ] Hilt bindings provided for new services/repositories.
- [ ] Unit tests for domain/data; Compose tests for UI as needed.
- [ ] KtLint passes (preBuild runs ktlintFormat).
- [ ] Public APIs documented with KDoc where helpful.


## 19. Example: Adding a New Feature (Outline)

1) Domain:
- Define domain models if needed.
- Create repository interface method(s).
- Implement use cases (e.g., AddFooUseCase, GetFooUseCase).

2) Data:
- Update entities/DAOs and migrations.
- Implement repository interface; map Entity↔Domain.

3) Presentation:
- Add ViewModel with UI state and event handlers.
- Create composables and navigation route.
- Wire ViewModel to use cases via Hilt.

4) Tests:
- Unit tests for use cases and repository implementation.
- UI tests for screens and navigation.


## 20. Roadmap Notes for Lazuli

- Consider extracting domain layer into app/src/main/java/com/softklass/lazuli/domain to formalize Clean Architecture.
- Gradually introduce use cases for key flows (e.g., CreateList, AddItem, RecognizeTextFromImage).
- Expand Resource/Result usage in UI to unify loading/error handling.


By following these guidelines, Lazuli will remain robust, testable, and pleasant to contribute to.
