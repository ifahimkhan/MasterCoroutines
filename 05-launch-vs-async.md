# Part 5: Launch vs Async

Both `launch` and `async` are functions in Kotlin to **start** a coroutine.

```kotlin
launch { }
async { }
```

So what is different?

## The one-line answer

- `launch` : **fire and forget**
- `async` : **perform a task and return a result**

## The post office analogy

- `launch` is like **posting a letter**. You drop it in the box and walk away. You get a receipt slip, but no reply comes back to you.
- `async` is like **ordering food with a token number**. You get a token. Later you show the token at the counter and collect your food.

That token is `Deferred`. Showing the token is `await()`.

## launch - fire and forget

```kotlin
val job = GlobalScope.launch(Dispatchers.Default) {
    // do something and do not return result
}
```

`launch` returns a **`Job`**.

A `Job` is the receipt slip. With it you can:
- check the status of the task
- cancel the task

```kotlin
job.cancel()
```

But a `Job` carries **no result value**.

Use `launch` when the answer is "just go do it": save to database, log an event, update the UI.

## async - give me the result

```kotlin
val deferredJob = GlobalScope.async(Dispatchers.Default) {
    // do something and return result, for example 10 as a result
    return@async 10
}

val result = deferredJob.await()  // result = 10
```

`async` returns a **`Deferred<T>`**.

`Deferred` is the token. Call `await()` on it to collect the value.

If you know Java, this is like `Future` - where you call `future.get()` to get the result.

And `Deferred` is also a `Job`, so you can still check status or cancel it.

> **Note:** I have used `GlobalScope` for quick examples. We should avoid using it at all costs. In an Android project, we should use custom scopes based on our use case, such as `lifecycleScope` and `viewModelScope`. Part 7 covers this.

## Doing two tasks in parallel

This is where `async` really shines.

Say we have two tasks, each taking 2 seconds.

**One after another (4 seconds):**

```kotlin
launch {
    val one = doTaskOne()   // 2 seconds
    val two = doTaskTwo()   // 2 more seconds
    showResult(one + two)   // total ~4000 ms
}
```

**Both at the same time (2 seconds):**

```kotlin
launch {
    val deferredOne = async { doTaskOne() }
    val deferredTwo = async { doTaskTwo() }
    val result = deferredOne.await() + deferredTwo.await()
    showResult(result)      // total ~2000 ms
}
```

**Analogy:** you need tea and a sandwich.

- Sequential: order tea, wait, drink it, then order the sandwich, wait.
- Parallel: order both, then collect both.

Same shop, half the time.

**Important:** start both `async` blocks **first**, and only then call `await()`. If you write `async { }.await()` on one line, you have gone back to sequential without noticing.

## The second difference - exceptions

This one surprises people.

Say we have a function that throws:

```kotlin
private fun doSomethingAndThrowException() {
    throw Exception("Some Exception")
}
```

**With `launch`:**

```kotlin
GlobalScope.launch {
    doSomethingAndThrowException()
}
```

It will **CRASH** the application, as expected.

**With `async`:**

```kotlin
GlobalScope.async {
    doSomethingAndThrowException()
}
```

The application will **NOT** crash. The exception is stored inside the `Deferred` and gets **silently dropped** unless we handle it.

**Analogy:** `launch` is a smoke alarm - it screams. `async` is a note left inside a sealed envelope - if nobody opens the envelope, nobody ever knows.

You open the envelope by calling `await()`.

Both can be handled with `try-catch`:

```kotlin
GlobalScope.launch {
    try {
        doSomethingAndThrowException()
    } catch (e: Exception) {
        // handle exception
    }
}
```

```kotlin
GlobalScope.async {
    try {
        doSomethingAndThrowException()
    } catch (e: Exception) {
        // handle exception
    }
}
```

Part 9 goes deeper into this.

## The table

| | launch | async |
|---|---|---|
| Purpose | Fire and forget | Perform a task and return a result |
| Returns | `Job` - no result value | `Deferred<T>` - `await()` gives the result |
| Exception | Crashes the app if not handled | Stored in the `Deferred`, silently dropped unless handled |

## Thumb-rules

- Need a result? Use `async` + `await()`.
- Don't need a result? Use `launch`.
- Both can run tasks in parallel.
- Start all `async` blocks first, `await()` afterwards.
- An unhandled exception in `launch` is loud. In `async` it is silent.

Next: [withContext](06-withcontext.md)

That's it for now.
