# Data Layer

**Location:** `app/src/main/java/com/ziro/fit/data/`

## STRUCTURE

```
data/
├── remote/         # ZiroApi (Retrofit)
├── local/          # TokenManager, UserSessionManager
├── model/          # API request/response models
└── repository/     # 17 Repository implementations
```

## REPOSITORIES

| Repository | Purpose |
|------------|---------|
| WorkoutRepository | Workout CRUD |
| BookingsRepository | Booking management |
| ClientRepository | Client data |
| BlogRepository | Blog posts |
| ExploreRepository | Trainer discovery |
| +12 others | Feature-specific |

## API

- Retrofit with auth interceptor
- SupabaseClient for realtime
- Token refresh handled in interceptor

## LOCAL STORAGE

- `TokenManager`: JWT token storage/refresh
- `UserSessionManager`: User session state
- No Room/DataStore - uses SharedPreferences via managers
