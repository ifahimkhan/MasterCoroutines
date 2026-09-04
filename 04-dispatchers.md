# Part 4: Dispatchers

At the end of Part 3 we said: `suspend` does not decide the thread.

So who decides? **Dispatchers.**

> Dispatchers help Coroutines in deciding the thread on which the task has to be done.

## The office analogy

Think of an office with three rooms:

- **The front desk** - faces the customer. Must always be free and smiling. Never give it heavy work.
- **The back office** - people here make phone calls, send emails, wait for replies. Lots of waiting, little thinking.
- **The lab** - people here do heavy calculation. No waiting, pure brain work.

Now, a Dispatcher is simply the person who says: *"This job goes to that room."*

Kotlin gives us these rooms ready-made.

## Dispatchers.Main - the front desk

The Android **UI thread**.

Use it for:
- Updating the UI
- Small, quick, in-memory work

```kotlin
launch(Dispatchers.Main) {
    // update the UI
}
```

Rule: get in, do the small thing, get out.

## Dispatchers.IO - the back office

**IO** means Input / Output. Work where we mostly **wait** for someone else.

Use it for:
- Network calls (downloading a file, hitting an API)
- Database read and write
- Reading and writing files
- Reading SharedPreferences

```kotlin
launch(Dispatchers.IO) {
    // network call or database work
}
```

Why is this a separate room? Because waiting is cheap. We can have many people waiting at once, so this pool is allowed to be **big**.

## Dispatchers.Default - the lab

CPU-intensive work. Data is already in memory, we just need to think hard about it.

Use it for:
- Sorting a large list
- Matrix calculation
- Parsing or transforming a big JSON already in memory
- Bitmap / image processing

```kotlin
launch(Dispatchers.Default) {
    // heavy calculation
}
```

This pool is **small** on purpose - roughly the number of CPU cores you have. More thinkers than brains does not help.

## Dispatchers.Unconfined - the odd one

It does not stick to any particular thread. It just continues on whichever thread is available at the moment.

Mostly useful in **unit testing**.

```kotlin
launch(Dispatchers.Unconfined) {
    // not confined to any specific thread
}
```

As a beginner: know it exists, don't reach for it.

## The full picture

| Dispatcher | Room | Use it for | Pool size |
|---|---|---|---|
| `Dispatchers.Main` | Front desk | UI updates, tiny work | 1 thread (the UI thread) |
| `Dispatchers.IO` | Back office | Network, database, files | Large |
| `Dispatchers.Default` | Lab | Heavy CPU work in memory | ~number of CPU cores |
| `Dispatchers.Unconfined` | No fixed room | Testing | - |

## If you know RxJava

Dispatchers in Kotlin Coroutines are like Schedulers in RxJava:

| Coroutines | RxJava |
|---|---|
| `Dispatchers.Default` | `Schedulers.computation()` |
| `Dispatchers.IO` | `Schedulers.io()` |
| `Dispatchers.Main` | `AndroidSchedulers.mainThread()` |

If you don't know RxJava, skip this table. You lose nothing.

## The pattern to remember

Every one of them follows the same shape:

```kotlin
launch(Dispatchers.WhichRoom) {
    // task that belongs in that room
}
```

## The mistake everyone makes once

Using `Dispatchers.IO` for heavy calculation, or `Dispatchers.Default` for network calls.

It will still work. Nothing crashes. But it wastes resources.

Easy way to decide - ask one question:

> **Is my code waiting, or is my code thinking?**

- **Waiting** (for server, disk, database) → `Dispatchers.IO`
- **Thinking** (sorting, math, image work) → `Dispatchers.Default`
- **Showing** (UI) → `Dispatchers.Main`

## Thumb-rules

- Dispatchers decide **which thread** the work runs on.
- `Main` = UI. `IO` = waiting. `Default` = thinking. `Unconfined` = testing.
- Never put heavy work on `Main`.
- Waiting or thinking - that one question picks your dispatcher.

Next: [Launch vs Async](05-launch-vs-async.md)

That's it for now.
