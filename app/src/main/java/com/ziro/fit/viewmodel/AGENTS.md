# ViewModel Layer

**Location:** `app/src/main/java/com/ziro/fit/viewmodel/`

## OVERVIEW

37 ViewModels managing UI state for all screens. Two complexity hotspots: `WorkoutViewModel` (881 lines) and `AuthViewModel` (528 lines).

## PATTERNS

### State Management
- **Primary**: `MutableStateFlow<T>` + `StateFlow<T>` for UI state
- **Secondary**: Compose `mutableStateOf` in AuthViewModel, UserViewModel
- **Pattern**: `_uiState.update { it.copy(...) }` for immutable updates

### DI
- All ViewModels use `@HiltViewModel` + `@Inject` constructor

### Coroutines
- `viewModelScope.launch` for async work
- Advanced: `tokenManager.activeMode.onEach {...}.launchIn(viewModelScope)`

### Error Handling
- `try/catch` + `ApiErrorParser.parse(e)` for consistent error messages
- Repository returns `Result`-like types with `onSuccess`/`onFailure`

## FILES

| File | Lines | Purpose |
|------|-------|---------|
| WorkoutViewModel.kt | 881 | Live workout session, voice commands |
| AuthViewModel.kt | 528 | Authentication, token management |
| ProfileViewModel.kt | ~250 | User profile |
| BookingsViewModel.kt | ~200 | Booking management |
| +33 others | <200 | Feature-specific state |

## REFACTORING CANDIDATES

- Extract `WorkoutSessionController` from WorkoutViewModel
- Extract `VoiceCommandHandler` from WorkoutViewModel
- Create `AuthDomain` use-cases for AuthViewModel
