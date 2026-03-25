# PROJECT KNOWLEDGE BASE

**Generated:** 2026-03-25
**Project:** ZiroFit Android

## OVERVIEW

Fitness tracking Android app with Jetpack Compose UI, Hilt DI, MVVM architecture. Single-module project (`:app` only). Kotlin-first with Retrofit for API communication.

THE SERVER NEXT.js APP IS LOCATED IN '../zirofit-next'
## STRUCTURE

```
./
├── app/src/main/java/com/ziro/fit/
│   ├── MainActivity.kt           # Entry point (single-activity app)
│   ├── ZiroFitApp.kt            # Application class (Hilt)
│   ├── auth/                    # Social auth (Google, Apple)
│   ├── data/                    # Repositories, API, local storage
│   ├── di/                      # Hilt modules
│   ├── model/                   # 36+ data classes
│   ├── service/                 # Background services
│   ├── ui/                      # 21+ feature packages (Compose)
│   ├── util/                    # Cross-cutting utilities
│   └── viewmodel/               # 37 ViewModels
├── app/src/test/                # Unit tests (JUnit + MockK)
├── app/src/androidTest/         # E2E tests (UIAutomator)
└── gradle/libs.versions.toml   # Version catalog
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add new screen | `ui/<feature>/` | Create composable + ViewModel |
| API call | `data/repository/` | Add method to existing repo |
| Auth logic | `viewmodel/AuthViewModel.kt` | 528 lines - complex |
| Workout logic | `viewmodel/WorkoutViewModel.kt` | 881 lines - largest file |
| Shared UI | `ui/components/` | Buttons, inputs, overlays |
| Theme | `ui/theme/` | Colors, typography |
| DI setup | `di/AppModule.kt` | Hilt module |
| Tests | `src/test/` (unit), `src/androidTest/` (E2E) | |

## ANTI-PATTERNS (THIS PROJECT)

- **Empty catch blocks**: Found in `AuthViewModel.kt` (lines 438, 468) and `ModeTabBar.kt` - MUST add error handling
- **TODO markers**: 28 files have TODO/DEPRECATED comments - create issues instead
- **Large ViewModels**: `WorkoutViewModel` (881 lines), `AuthViewModel` (528 lines) - candidates for refactor

## CONVENTIONS

- **DI**: Hilt with `@HiltViewModel` and `@Inject` constructors
- **State**: StateFlow or Compose `mutableStateOf` - no LiveData
- **Coroutines**: `viewModelScope.launch` for async work
- **Error handling**: Use `ApiErrorParser.parse(e)` for consistent messages
- **Testing**: MockK for mocks, `MainDispatcherRule` for coroutines
- **Navigation**: Callback-based (no central NavHost), some screens use NavController directly

## BUILD

```bash
./gradlew assembleDebug     # Debug build
./gradlew assembleRelease   # Release build
./gradlew test             # Unit tests
./gradlew connectedAndroidTest  # E2E tests
```

## NOTES

- No ktlint/detekt configured - code style is default Kotlin/Android
- No CI workflows in repo - needs GitHub Actions setup
- API keys via `local.properties` with secrets plugin - not CI-friendly
- Single-activity architecture with Compose navigation
- Deep-linked entry points in MainActivity
