# Part 3: Suspend Function

In Part 2, we saw code that looks synchronous but runs asynchronously.

The keyword that makes that magic possible is `suspend`.

## What is a suspend function?

> A suspend function is a function that can be **started**, **paused**, and **resumed**.

That's it. Three words: start, pause, resume.

## The bookmark analogy

You are reading a book. Your phone rings.

You do **not** burn the book. You do **not** sit staring at the page.

You put a **bookmark**, close the book, take the call, then open the book at the exact same line and continue.

A suspend function does the same thing:

- Bookmark = where it paused
- Take the call = the thread goes and does other work
- Continue = it resumes from the exact same line

**The key point:** while the book is closed, **you are free**. You are not stuck.

That is the difference between **suspending** and **blocking**.

## Suspend vs Block - the important difference

This is the one thing beginners mix up. Let's make it clear.

| | Blocking | Suspending |
|---|---|---|
| What happens to the thread | Thread is stuck, doing nothing | Thread is free, goes and does other work |
| Example | `Thread.sleep(2000)` | `delay(2000)` |
| Cost | Wastes a whole worker | Wastes nothing |

**Analogy:** you order tea at a shop.

- **Blocking:** you stand at the counter frozen for 5 minutes until the tea is ready. The shopkeeper cannot serve anyone else, because you are in front of him.
- **Suspending:** you take a token and sit down. The shopkeeper serves 4 other people. When your tea is ready, your number is called and you continue.

Same waiting time. Very different use of the shopkeeper.

## How to write one

Just add the `suspend` keyword in front of `fun`.

```kotlin
suspend fun fetchUser(): User {
    return withContext(Dispatchers.IO) {
        // make network call on IO thread
        // return user
    }
}
```

## The one rule of suspend functions

> A suspend function can only be called from **another suspend function**, or from **inside a coroutine**.

Why? Because pausing and resuming needs someone who knows how to pause and resume. A normal function has no idea how to do that.

**Analogy:** only a person holding the bookmark can reopen the book. A random passerby cannot.

## So how do we call it from `onCreate`?

`onCreate` is a normal Android function. We cannot add `suspend` to it.

So we start a coroutine, and call the suspend function inside it.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GlobalScope.launch(Dispatchers.Main) {
        val user = fetchUser()  // fetch on IO thread
        showUser(user)          // back on UI thread
    }
}
```

`launch { }` opens the door. Inside that door, suspend functions are allowed.

> **Note:** again, `GlobalScope` is only for quick examples. Use `lifecycleScope` in an Activity. Part 7 explains why.

## Let's trace the control

This example shows exactly how control moves.

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.Main) {
            doSomething()        // non-suspend, UI thread
            doLongRunningTask()  // suspend, background thread
            doSomethingElse()    // non-suspend, UI thread
        }
    }

    fun doSomething() {
    }

    fun doSomethingElse() {
    }

    suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            // long running work
            delay(2000) // added delay to simulate
        }
    }
}
```

Step by step:

1. We launched with `Dispatchers.Main`, so we start on the **UI thread**.
2. `doSomething()` is a normal function - it runs on the **UI thread**.
3. `doLongRunningTask()` is a suspend function using `Dispatchers.Default` - the work moves to a **background thread**. The UI thread is now **free**, so the screen keeps scrolling smoothly.
4. When that work finishes, control comes **back to the UI thread**.
5. `doSomethingElse()` runs on the **UI thread**.

Three lines, written top to bottom. Two thread switches happened. We wrote zero callbacks.

**This is the beauty of Kotlin Coroutines.**

## A common beginner mistake

Adding `suspend` does **not** move work to a background thread by itself.

```kotlin
suspend fun wrong(): User {
    // still runs on whatever thread called it
    return heavyWork()
}
```

`suspend` only means "this function can pause".

**Which thread it runs on is decided by the Dispatcher.** That is our next part.

## Thumb-rules

- `suspend` = can start, pause, and resume.
- Suspending frees the thread. Blocking wastes it.
- Use `delay()`, not `Thread.sleep()`, inside coroutines.
- A suspend function is callable only from a coroutine or another suspend function.
- `suspend` alone does not change the thread.

Next: [Dispatchers](04-dispatchers.md)

That's it for now.
