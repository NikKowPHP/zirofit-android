# UI Layer

**Location:** `app/src/main/java/com/ziro/fit/ui/`

## OVERVIEW

21+ feature packages using Jetpack Compose. No central NavHost - uses callback-based navigation + occasional NavController.

## STRUCTURE

```
ui/
├── theme/           # Colors, Typography, Theme
├── components/      # Reusable: ZiroButton, ZiroTextField, etc.
├── onboarding/      # Role selection, onboarding flow
├── auth/           # Login, register, password screens
├── dashboard/      # Main dashboard
├── workouts/       # Workout list, templates
├── workout/        # Live workout screen
├── profile/        # User profile, settings
├── discovery/      # Trainer/explore
├── bookings/       # Booking management
├── client/         # Client-specific screens
├── admin/          # Admin features
└── ...             # 21 total feature packages
```

## PATTERNS

### Navigation
- **Primary**: Callback-based (`onNavigateToX: () -> Unit`)
- **Secondary**: NavController in some screens (AICoachScreen)
- **No central NavHost** - host layer wires navigation

### State
- `val uiState by viewModel.uiState.collectAsState()`
- ViewModel injected via `hiltViewModel()`

### Components
- Shared in `ui/components/`: ZiroButton, ZiroTextField, ModeTabBar
- Overlays in `ui/components/overlays/`: RpePicker, PlateCalculator

## TESTING

- Unit tests in `app/src/test/java/com/ziro/fit/viewmodel/`
- E2E tests in `app/src/androidTest/java/com/ziro/fit/e2e/`
- Uses UIAutomator + BaseE2ETest pattern
