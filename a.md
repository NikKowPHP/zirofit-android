missed featuers




Based on a comprehensive analysis of both the iOS (`Ziro Fit/`) and Android (`app/src/main/`) codebases, here is the prioritized list of features currently missing or incomplete in the Android app compared to the iOS app.

### P0: Critical Core & Revenue Blockers
*These features directly impact user acquisition, core workout experience, and trainer monetization.*

1.  **Social Authentication (Google & Apple Sign-In)**
    *   **iOS:** Fully implemented using `GoogleSignInHelper.swift` and `AppleSignInHelper.swift` integrated with Supabase.
    *   **Android:** Currently only supports email/password (`LoginScreen.kt`). 
    *   *Impact:* High friction for new user onboarding.
2.  **Offline Sync Engine**
    *   **iOS:** `SyncManager.swift` implements a robust background queue (`SyncAction`) that caches set logs and session completions locally if the network drops, syncing them when connectivity is restored.
    *   **Android:** `LiveWorkoutRepository.kt` makes direct API calls. If the network drops during a workout, sets will fail to log.
    *   *Impact:* Critical for gym environments where WiFi/Cellular can be spotty.
3.  **Plate Calculator & RPE Picker**
    *   **iOS:** Native `PlateCalculatorOverlay.swift` (visual barbell math) and `RPEPickerOverlay.swift` (Rate of Perceived Exertion scale).
    *   **Android:** Currently stubbed out with `"Coming Soon"` text inside `LiveWorkoutScreen.kt`.
    *   *Impact:* Core functionality for serious lifters.
4.  **Trainer Revenue & Payouts Dashboard**
    *   **iOS:** Rich `RevenueView.swift` (earnings charts, recent transactions) and `PayoutsView.swift` (Stripe Connect management).
    *   **Android:** Missing these dedicated screens entirely (trainers cannot view earnings or manage their Stripe payouts natively).

### P1: High Impact & Differentiating Features
*These features are primary selling points of the platform or significantly boost retention.*

5.  **AI Voice Logging**
    *   **iOS:** `VoiceLogManager.swift` uses `SFSpeechRecognizer` to allow users to dictate sets hands-free (e.g., *"10 reps of bench press at 80kg"*), parsed via regex/fuzzy matching into the workout session.
    *   **Android:** Not implemented.
6.  **Customizable Analytics Dashboard & Heatmaps**
    *   **iOS:** `PersonalAnalyticsViewModel.swift` and `ManageWidgetsView.swift` allow clients to reorder, add, and remove specific analytic widgets (Volume Progression, Consistency, Heat Map, Muscle Focus).
    *   **Android:** `ClientStatisticsContent.kt` has a static, hardcoded layout with basic Canvas charts. Missing the Heatmap and customizable widget grid.
7.  **Trainer Matchmaking Wizard**
    *   **iOS:** `TrainerFindingOnboardingView.swift` provides a multi-step, paginated questionnaire to match clients with trainers based on goals and location.
    *   **Android:** Missing the wizard; jumps straight into the raw `TrainerDiscoveryScreen.kt` list/map.

### P2: Engagement & UX Parity
*Features that drive daily active usage and improve platform feel.*

8.  **Daily Targets / Habit Tracking**
    *   **iOS:** `DailyTargetManager.swift` and `AddDailyTargetView.swift` allow users to set standalone daily challenges (e.g., "50 Pushups a day") separate from full workout templates.
    *   **Android:** Completely missing.
9.  **Map Clustering for Discovery**
    *   **iOS:** `TrainerMapView.swift` implements custom `ClusterItem` logic to dynamically group trainers and events on the map when zoomed out.
    *   **Android:** Uses standard Google Maps markers without clustering, which will become cluttered as the platform scales.
10. **In-App Localization Engine**
    *   **iOS:** `LanguageManager.swift` dynamically switches the app language without requiring a device-level locale change.
    *   **Android:** Strings are currently hardcoded directly into Compose files (e.g., `Text("Search events...")`), requiring a massive string extraction to `strings.xml` before localization can begin.

### P3: Polish & System Integrations
*Nice-to-haves that make the app feel premium.*

11. **Centralized Haptics Engine**
    *   **iOS:** `HapticManager.swift` provides consistent, graded haptic feedback (soft, light, medium, heavy) across the entire app (e.g., when completing a set or swiping).
    *   **Android:** Haptics are not systematically implemented in the Compose UI.
12. **Live Activities / Ongoing Notifications**
    *   **iOS:** `LiveActivityManager.swift` and `WorkoutLiveActivityWidget.swift` provide a rich Lock Screen/Dynamic Island experience to track rest timers and sets without opening the app.
    *   **Android:** Uses a basic `NotificationCompat.Builder` in `ActiveWorkoutService.kt`. It should be upgraded to use Android's newer MediaStyle or Custom CustomContentViews for a comparable lock-screen experience.
13. **Fluid Mode Switching**
    *   **iOS:** Users can swipe the bottom tab bar to fluidly animate between Personal and Professional modes (`AppState.isModeSelectorExpanded`).
    *   **Android:** Uses a rigid navigation graph replacement; lacks the fluid gesture-based mode switcher.