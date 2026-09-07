# Learn Kotlin Coroutines with Jetpack Compose

A Jetpack Compose port of [Learn-Kotlin-Coroutines](https://github.com/amitshekhariitbhu/Learn-Kotlin-Coroutines)
by Amit Shekhar. Same real-world examples, rebuilt with `StateFlow`, `viewModelScope`,
Navigation Compose and Material 3 instead of Activities, LiveData and XML layouts.

## What you will learn

| Example | Concept | ViewModel |
|---------|---------|-----------|
| Single Network Call | One `suspend` Retrofit call in `viewModelScope` | [SingleNetworkCallViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/retrofit/single/SingleNetworkCallViewModel.kt) |
| Series Network Calls | Sequential suspend calls | [SeriesNetworkCallsViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/retrofit/series/SeriesNetworkCallsViewModel.kt) |
| Parallel Network Calls | `async` / `await` + `CoroutineExceptionHandler` | [ParallelNetworkCallsViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/retrofit/parallel/ParallelNetworkCallsViewModel.kt) |
| Room Database | Suspend DAO, DB-first then API fallback | [RoomDBViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/room/RoomDBViewModel.kt) |
| Long Running Task | `withContext(Dispatchers.Default)` | [LongRunningTaskViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/task/onetask/LongRunningTaskViewModel.kt) |
| Two Long Running Tasks | Parallel `async` with combined result | [TwoLongRunningTasksViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/task/twotasks/TwoLongRunningTasksViewModel.kt) |
| Timeout | `withTimeout` and `TimeoutCancellationException` | [TimeoutViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/timeout/TimeoutViewModel.kt) |
| Try-Catch | Error handling with try / catch | [TryCatchViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/errorhandling/trycatch/TryCatchViewModel.kt) |
| CoroutineExceptionHandler | Error handling via the coroutine context | [ExceptionHandlerViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/errorhandling/exceptionhandler/ExceptionHandlerViewModel.kt) |
| Ignore Error And Continue | `supervisorScope` | [IgnoreErrorAndContinueViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/errorhandling/supervisor/IgnoreErrorAndContinueViewModel.kt) |
| Basics Playground | 23 experiments: scopes, dispatchers, launch vs async, cancellation, exceptions, suspending vs blocking. Output shown in an on-screen log console. | [BasicViewModel](app/src/main/java/com/fahim/learncoroutinesbytutorials/ui/basic/BasicViewModel.kt) |

## Unit tests

ViewModels are tested with `kotlinx-coroutines-test` (`runTest`, `StandardTestDispatcher`,
virtual time) and hand-written fakes, no mocking library:

- [MainDispatcherRule](app/src/test/java/com/fahim/learncoroutinesbytutorials/utils/MainDispatcherRule.kt) swaps `Dispatchers.Main`
- [FakeApiHelper](app/src/test/java/com/fahim/learncoroutinesbytutorials/utils/FakeApiHelper.kt) / [FakeDatabaseHelper](app/src/test/java/com/fahim/learncoroutinesbytutorials/utils/FakeDatabaseHelper.kt)
- Tests under [app/src/test](app/src/test/java/com/fahim/learncoroutinesbytutorials) cover every ViewModel, including
  timing assertions that prove series calls take 2x and parallel calls take 1x.

```
./gradlew testDebugUnitTest
```

## Stack

- Kotlin, Jetpack Compose, Material 3, Navigation Compose
- `kotlinx-coroutines`, `StateFlow`, `collectAsStateWithLifecycle`
- Retrofit + Gson, Room (KSP), Coil
- AGP 9 built-in Kotlin, Gradle 9.3

## Project layout

```
app/src/main/java/com/fahim/learncoroutinesbytutorials
├── data
│   ├── api        Retrofit service, ApiHelper
│   ├── local      Room database, DAO, entity, DatabaseHelper
│   └── model      ApiUser
├── ui
│   ├── base       UiState, ViewModelFactory
│   ├── components Shared composables (scaffold, user list, log console)
│   ├── navigation Screen enum, AppNavHost
│   ├── home       Example picker
│   ├── basic      Basics playground
│   ├── retrofit   single / series / parallel
│   ├── room
│   ├── task       onetask / twotasks
│   ├── timeout
│   └── errorhandling  trycatch / exceptionhandler / supervisor
└── util           UiState helpers
```

## Credits

All example scenarios come from Amit Shekhar's
[Learn-Kotlin-Coroutines](https://github.com/amitshekhariitbhu/Learn-Kotlin-Coroutines)
(Apache 2.0). Read his blog [Mastering Kotlin Coroutines](https://outcomeschool.com/blog/kotlin-coroutines)
alongside this code.
