# Part 6: withContext

We have seen `launch` and `async`. Both **create a new coroutine**.

Now meet the third one, which does something different.

> `withContext` is a suspend function through which we can do a task by providing the Dispatcher on which we want the task to be done.

The key sentence:

> `withContext` does **not** create a new coroutine. It only **shifts the context** of the existing coroutine.

## The analogy

You are already at work. A job needs to be done in the lab.

- `launch` / `async` = **hire a new person** to go do it.
- `withContext` = **you yourself walk into the lab**, do the work, and walk back to your desk.

Same person. Different room. Nobody new was hired.

## The basic form

```kotlin
private suspend fun doLongRunningTaskAndDoNotReturnResult() {
    withContext(Dispatchers.Default) {
        // your code for doing a long running task
        delay(2000) // added delay to simulate
    }
}
```

It can also return a result:

```kotlin
private suspend fun doLongRunningTask(): Int {
    return withContext(Dispatchers.Default) {
        // your code for doing a long running task
        delay(2000) // added delay to simulate
        return@withContext 10
    }
}
```

Since `withContext` is a suspend function, it can only be called from a suspend function or a coroutine. That is why both functions above are marked `suspend`.

Using it:

```kotlin
GlobalScope.launch(Dispatchers.Main) {
    val result = doLongRunningTask()
    showResult(result)  // back on UI thread
}
```

Notice: after `withContext` finishes, we are automatically **back on the UI thread**. We did not write a single line to come back.

## withContext is sequential

Two tasks, 2 seconds each:

```kotlin
GlobalScope.launch(Dispatchers.Main) {
    val resultOne = doLongRunningTaskOne()
    val resultTwo = doLongRunningTaskTwo()
    showResult(resultOne + resultTwo)
}
```

This takes **about 4000 milliseconds**. Task two starts only after task one is done.

That is expected - one person cannot be in two rooms at once.

For parallel work, we need two coroutines, so we go back to `async`:

```kotlin
GlobalScope.launch {
    val deferredOne = async { doLongRunningTaskOne() }
    val deferredTwo = async { doLongRunningTaskTwo() }
    val result = deferredOne.await() + deferredTwo.await()
    showResult(result)
}
```

Now it takes **about 2000 milliseconds**.

## The best pattern in Android

This is the pattern you will use every day. Learn this shape:

```kotlin
// The caller does not care about threads at all
lifecycleScope.launch(Dispatchers.Main) {
    val user = fetchUser()
    showUser(user)
}

// The function itself decides where its work belongs
suspend fun fetchUser(): User {
    return withContext(Dispatchers.IO) {
        // network call
    }
}
```

The rule behind it: **a suspend function should be safe to call from anywhere.**

The function knows it needs IO. So the function puts `withContext(Dispatchers.IO)` inside itself. The caller just calls it and stays clean.

This is called being **main-safe**.

## The three, side by side

| | Creates a new coroutine? | Returns | Runs in parallel? | Is it a suspend function? |
|---|---|---|---|---|
| `launch` | Yes | `Job` | Yes | No |
| `async` | Yes | `Deferred<T>` | Yes | No |
| `withContext` | No | The result directly | No (sequential) | Yes |

## Thumb-rules

- `launch` and `async` create coroutines. `withContext` does not.
- `withContext` just moves the current coroutine to another Dispatcher and brings it back.
- `withContext` is sequential. For parallel work, use `async`.
- Put `withContext` **inside** the suspend function, so callers never worry about threads.

Next: [Scopes](07-scopes.md)

That's it for now.
