# Part 2: Why do we need Coroutines?

In Part 1, we learned what coroutines are.

Now let's see the actual problem they solve. Once you feel the problem, the solution sticks.

## The very standard use case

Almost every app does this:

1. Fetch the user from the server.
2. Show the user on the screen.

Simple. Let's write it.

```kotlin
fun fetchAndShowUser() {
    val user = fetchUser()
    showUser(user)
}

fun fetchUser(): User {
    // make network call
    // return user
}

fun showUser(user: User) {
    // show user
}
```

Looks clean. But run it in Android and it crashes with:

```
NetworkOnMainThreadException
```

## Why does it crash?

Android has one special thread called the **main thread** (also called the UI thread).

Everything you see - buttons, scrolling, animation - is drawn by this one thread.

**The analogy:** the main thread is the cashier at a shop counter.

A network call takes 2 seconds. If the cashier goes to the warehouse himself to fetch a box, the counter is empty for 2 seconds. Nobody can pay. The queue freezes.

That frozen queue is your frozen app.

So Android simply refuses. It says: *do not do network work on the main thread.*

Fine. So we move the work to a background thread. But now a new problem appears.

## Solution 1: Callbacks

We do the work in the background and get the result through a callback.

```kotlin
fun fetchAndShowUser() {
    fetchUser { user ->
        showUser(user)
    }
}

fun fetchUser(callback: (User) -> Unit) {
    // make network call on background thread to get user
    callback(user)
}
```

This works. The app does not freeze.

But look what happens when one call depends on another.

```kotlin
fun fetchData() {
    fetchA { a ->
        fetchB(a) { b ->
            fetchC(b) { c ->
                // do something with c
            }
        }
    }
}
```

See the shape? It keeps sliding to the right.

This is called **callback hell**.

**The analogy:** you ask a friend to call you back. When he calls, you ask him to call another friend to call you back. When that friend calls, you ask him to call a third friend. Now try adding error handling to that chain. It becomes a mess.

Now imagine adding a `try-catch` to each level. Now imagine cancelling all of it when the user leaves the screen. That is the real pain.

## Solution 2: RxJava

RxJava removes the nesting.

```kotlin
fetchUser()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { user ->
        showUser(user)
    }
```

Better. No nesting.

But it comes with a price: a big new vocabulary. Operators, Observables, Singles, Schedulers, disposables. Powerful, but a lot to learn just to make one network call.

## Solution 3: Coroutines

Now the same thing with coroutines.

```kotlin
fun fetchAndShowUser() {
    GlobalScope.launch(Dispatchers.Main) {
        val user = fetchUser()  // fetch on IO thread
        showUser(user)          // back on UI thread
    }
}

suspend fun fetchUser(): User {
    return withContext(Dispatchers.IO) {
        // make network call on IO thread
        // return user
    }
}

fun showUser(user: User) {
    // show user
}
```

Look at these two lines:

```kotlin
val user = fetchUser()
showUser(user)
```

Top to bottom. One line after another. Exactly like our very first broken version.

**But it does not freeze the app.**

The code **looks synchronous**, but it **is asynchronous**.

That single sentence is the reason coroutines won.

> **Note:** I have used `GlobalScope` here for a quick example. In a real Android project, we should avoid `GlobalScope` at all costs. We should use scopes like `lifecycleScope` and `viewModelScope`. We will learn why in Part 7.

## Compare the three

| Approach | Nesting | Learning curve | Reads like normal code |
|---|---|---|---|
| Callbacks | Grows to the right (callback hell) | Small | No |
| RxJava | Flat | Large | Somewhat |
| Coroutines | Flat | Small | Yes |

## Thumb-rules

- The main thread draws the UI. Never make it wait.
- Callbacks fix freezing but create callback hell.
- RxJava fixes nesting but brings a big vocabulary.
- Coroutines fix both - async code that reads top to bottom.

Next: [Suspend Function](03-suspend-function.md)

That's it for now.
