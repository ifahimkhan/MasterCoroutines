package com.fahim.learncoroutinesbytutorials.ui.navigation

/**
 * Every destination in the app. `title` and `description` feed the home list.
 */
enum class Screen(val route: String, val title: String, val description: String) {
    Home("home", "Learn Kotlin Coroutines", ""),
    SingleNetworkCall(
        "single_network_call",
        "Single Network Call",
        "One suspend Retrofit call inside viewModelScope"
    ),
    SeriesNetworkCalls(
        "series_network_calls",
        "Series Network Calls",
        "Two calls one after another, second depends on first"
    ),
    ParallelNetworkCalls(
        "parallel_network_calls",
        "Parallel Network Calls",
        "Two calls at once with async / await"
    ),
    RoomDB(
        "room_db",
        "Room Database",
        "Read from DB, fall back to API, insert, show"
    ),
    LongRunningTask(
        "long_running_task",
        "Long Running Task",
        "5 second job on Dispatchers.Default"
    ),
    TwoLongRunningTasks(
        "two_long_running_tasks",
        "Two Long Running Tasks",
        "Two 2 second jobs in parallel, combined result"
    ),
    Timeout(
        "timeout",
        "Timeout",
        "withTimeout cancels a slow call"
    ),
    TryCatch(
        "try_catch",
        "Try-Catch Error Handling",
        "Handle a failing call with try / catch"
    ),
    ExceptionHandler(
        "exception_handler",
        "CoroutineExceptionHandler",
        "Handle a failing call with a handler in the context"
    ),
    IgnoreErrorAndContinue(
        "ignore_error_and_continue",
        "Ignore Error And Continue",
        "supervisorScope: one child fails, the other survives"
    ),
    Basic(
        "basic",
        "Basics Playground",
        "23 small experiments: scopes, dispatchers, cancel, exceptions"
    );

    companion object {
        /** Ordered list shown on the home screen. */
        val examples: List<Screen> = entries.filter { it != Home }
    }
}
