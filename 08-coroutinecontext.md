# Part 8: CoroutineContext

We have used Dispatchers. We have used Jobs. We have used scopes.

Now let's see the thing that holds them all together.

> `CoroutineContext` is an interface in Kotlin Coroutines that defines the **context**, or the **environment**, in which a coroutine runs.

## The backpack analogy

Every coroutine carries a small **backpack**.

Inside the backpack there are up to four things:

| Item | What it does |
|---|---|
| **Dispatcher** | Which thread the work runs on |
| **Job** | The lifecycle handle - cancel it, check if it's done |
| **CoroutineName** | A name, useful while debugging |
| **CoroutineExceptionHandler** | What to do if an uncaught exception happens |

That backpack is the `CoroutineContext`.

You have been packing this backpack all along, without noticing.

```kotlin
launch(Dispatchers.IO) { }
```

Here you packed exactly one item: the Dispatcher. Kotlin filled the rest with defaults.

## Combining items with `+`

Each item (`Job`, `Dispatcher`, `CoroutineName`, `CoroutineExceptionHandler`) is a `CoroutineContext.Element`.

Elements are combined with the plus operator.

**Just a Dispatcher:**

```kotlin
launch(Dispatchers.IO) {
    // work on IO thread
}
```

**Dispatcher + a name:**

```kotlin
launch(Dispatchers.IO + CoroutineName("FetchUser")) {
    // work on IO thread, named for easier debugging
}
```

**Dispatcher + name + exception handler:**

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    Log.d(TAG, "$exception handled!")
}

launch(Dispatchers.IO + CoroutineName("FetchUser") + handler) {
    // fully described environment
}
```

**Pack it once, reuse it:**

```kotlin
val myContext = Dispatchers.IO + CoroutineName("FetchUser") + handler

launch(myContext) {
    // same environment, reused
}
```

Cleaner when several coroutines share the same setup.

## Why `+` and not a list?

Because `+` also means **replace**.

```kotlin
val context = Dispatchers.IO + Dispatchers.Default
// Dispatchers.Default wins
```

Only one item of each type can be in the backpack. Add a second Dispatcher and it **replaces** the first one.

**Analogy:** the backpack has one labelled pocket per item. Put a new water bottle in the bottle pocket and the old one comes out.

## The context is inherited

Start a coroutine inside another coroutine, and the child gets a **copy of the parent's backpack**.

```kotlin
launch(Dispatchers.IO + CoroutineName("Parent")) {
    launch {
        // this child also runs on Dispatchers.IO
    }
}
```

The child did not ask for `Dispatchers.IO`. It inherited it.

Anything you pass explicitly to the child overrides just that one item.

```kotlin
launch(Dispatchers.IO + CoroutineName("Parent")) {
    launch(Dispatchers.Main) {
        // Dispatcher is Main now, but name still comes from the parent
    }
}
```

One exception: the **`Job` is never inherited**. Every coroutine gets its own new `Job`, which becomes a **child** of the parent's `Job`.

That parent-child chain of Jobs is what makes cancellation flow downward - cancel the parent and every child dies with it. That is the machinery behind Part 7.

## Where you actually feel this

Most days you write `launch(Dispatchers.IO)` and never think about context.

You will care about it when you:
- Want readable names in a debug log → `CoroutineName`
- Want one place to catch errors → `CoroutineExceptionHandler`
- Want to cancel a specific group of work → a custom `Job`

## Thumb-rules

- `CoroutineContext` = the backpack a coroutine carries.
- Four items: `Job`, `Dispatcher`, `CoroutineName`, `CoroutineExceptionHandler`.
- Combine with `+`. Same type added twice → the newer one replaces the older.
- Children inherit the parent's context, except the `Job`.

Next: [Exception Handling](09-exception-handling.md)

That's it for now.
