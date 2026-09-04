# Part 9: Exception Handling

Exception handling is another important topic. We must learn this.

Network calls fail. Servers go down. Wi-Fi drops. It **will** happen.

## When using launch

Take a suspend function that might fail:

```kotlin
suspend fun fetchUserAndSaveInDatabase() {
    withContext(Dispatchers.IO) {
        // fetch user
        // save in database
    }
}
```

### Way 1: try-catch

```kotlin
launch(Dispatchers.Main) {
    try {
        fetchUserAndSaveInDatabase()  // work on IO thread, back on UI thread
    } catch (exception: Exception) {
        Log.d(TAG, "$exception handled!")
    }
}
```

Simple, and it reads exactly like normal Kotlin code. This is the one you will use most.

### Way 2: CoroutineExceptionHandler

First, create the handler:

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    Log.d(TAG, "$exception handled!")
}
```

Then attach it while launching:

```kotlin
launch(Dispatchers.Main + handler) {
    fetchUserAndSaveInDatabase()
}
```

Any exception inside will be caught by the handler.

**Analogy:** `try-catch` is a bucket you hold under one specific pipe. `CoroutineExceptionHandler` is the drain at the bottom of the room - it catches whatever leaks from anywhere.

Use the handler when several coroutines should report failures in the same way.

## When using async

Remember from Part 5: an exception inside `async` is **silent** until you call `await()`.

So the `try-catch` goes **around `await()`**:

```kotlin
val deferredUser = async {
    fetchUser()
}

try {
    val user = deferredUser.await()
} catch (exception: Exception) {
    Log.d(TAG, "$exception handled!")
}
```

Wrapping only the `async { }` block does nothing. The envelope is opened at `await()`.

## Real case: two network calls, one after another

```kotlin
launch {
    try {
        val users = getUsers()
        val moreUsers = getMoreUsers()
    } catch (exception: Exception) {
        Log.d(TAG, "$exception handled!")
    }
}
```

If either call fails, we jump straight to the catch block. We lose both results.

Sometimes that is fine. Sometimes it is not.

## Real case: I want a partial result

Suppose we want an **empty list** for the call that failed, and still keep the result of the other one.

Give each call its own `try-catch`:

```kotlin
launch {
    val users = try {
        getUsers()
    } catch (e: Exception) {
        emptyList<User>()
    }

    val moreUsers = try {
        getMoreUsers()
    } catch (e: Exception) {
        emptyList<User>()
    }
}
```

Now one failure does not wipe out the other result.

## Now in parallel - coroutineScope

Same two calls, but running at the same time:

```kotlin
launch {
    try {
        coroutineScope {
            val usersDeferred = async { getUsers() }
            val moreUsersDeferred = async { getMoreUsers() }
            val users = usersDeferred.await()
            val moreUsers = moreUsersDeferred.await()
        }
    } catch (exception: Exception) {
        Log.d(TAG, "$exception handled!")
    }
}
```

If any call fails, we go to the catch block - and the other call is **cancelled** too.

## Partial result in parallel - supervisorScope

Want the other call to survive? Use `supervisorScope`, with a `try-catch` on each individual `await()`:

```kotlin
launch {
    supervisorScope {
        val usersDeferred = async { getUsers() }
        val moreUsersDeferred = async { getMoreUsers() }

        val users = try {
            usersDeferred.await()
        } catch (e: Exception) {
            emptyList<User>()
        }

        val moreUsers = try {
            moreUsersDeferred.await()
        } catch (e: Exception) {
            emptyList<User>()
        }
    }
}
```

One fails, the other still delivers.

## coroutineScope vs supervisorScope

This is the heart of it:

> `coroutineScope` cancels **everything** when any one of its children fails.
> `supervisorScope` lets the **other children continue** when one fails.

**Analogy - a group project:**

- `coroutineScope` = *"If one member fails, the whole submission is cancelled."* Strict team.
- `supervisorScope` = *"If one member fails, everyone else still submits their part."* Forgiving team.

**Analogy - loading a screen:**

- Loading the user profile fails → the screen is useless → `coroutineScope`.
- Loading "recommended for you" fails, but the main feed loaded fine → still show the feed → `supervisorScope`.

## Conclusion

- While **NOT** using `async`, go with `try-catch` or `CoroutineExceptionHandler`.
- While using `async`, in addition to `try-catch`, you have two options: `coroutineScope` and `supervisorScope`.
- With `async`, use **`supervisorScope` + individual `try-catch`** when you want to continue with the other tasks if one fails.
- With `async`, use **`coroutineScope` + a top-level `try-catch`** when you do **not** want to continue if any of them fails.

## Thumb-rules

- `launch` crashes loudly. `async` fails silently until `await()`.
- With `async`, always wrap `await()`, not the `async { }` block.
- All-or-nothing → `coroutineScope`.
- Best-effort, partial results → `supervisorScope`.

Next: [Flow - The Basics](10-flow-basics.md)

That's it for now.
