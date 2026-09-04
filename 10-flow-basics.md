# Part 10: Flow - The Basics

Everything so far returned **one** value.

```kotlin
suspend fun fetchUser(): User
```

Call it, wait, get one user. Done.

But some things are not one value.

- A search box - the user types, and types, and types.
- A database table - rows change while the screen is open.
- A download - 10%, 40%, 70%, 100%.

For those, we need something that can give us **many values over time**.

That is **Flow**.

## The analogy

- A **suspend function** is a **parcel**. It arrives once. You open it. Story over.
- A **Flow** is a **water pipe**. Open the tap and water keeps coming until you close it.

Or, if you prefer:

- suspend function = a **photo**
- Flow = a **video**

## The smallest possible Flow

```kotlin
fun getNumbers(): Flow<Int> = flow {
    emit(1)
    emit(2)
    emit(3)
}
```

`emit()` = push one value into the pipe.

Now use it:

```kotlin
lifecycleScope.launch {
    getNumbers().collect { value ->
        Log.d(TAG, "Got $value")
    }
}
```

`collect { }` = stand at the end of the pipe and catch whatever comes out.

Output:

```
Got 1
Got 2
Got 3
```

## The most important rule of Flow

> **A Flow does nothing until you collect it.**

Writing `getNumbers()` runs zero code. The `flow { }` block only starts when someone calls `collect`.

This is called being **cold**.

**Analogy:** a pipe with the tap closed. The water is available, but nothing flows until you open the tap. `collect` is opening the tap.

Two collectors = two separate streams, each starting from the beginning. Two people, two taps, two glasses.

## Flow is built on suspend

Because a Flow can pause between emissions, `collect` is a **suspend function**.

That means: **you can only collect a Flow inside a coroutine.**

Everything you learned in Parts 3 to 7 still applies here. Flow is not a new world - it is coroutines with more than one value.

## Operators - changing the water on the way

You can transform values while they travel through the pipe.

```kotlin
getNumbers()
    .map { it * 10 }         // 10, 20, 30
    .filter { it > 10 }      // 20, 30
    .collect { value ->
        Log.d(TAG, "Got $value")
    }
```

Output:

```
Got 20
Got 30
```

**Analogy:** a filter attached to the pipe. Water goes in, cleaner water comes out. The pipe itself never changed.

Only `collect` actually starts the flow. `map` and `filter` just describe what should happen.

## Choosing the thread

Same idea as Dispatchers, one new word.

```kotlin
getNumbers()
    .flowOn(Dispatchers.IO)   // the emitting side runs on IO
    .collect { value ->
        // collected on whichever thread the coroutine is on
    }
```

`flowOn` changes the thread for everything **above** it in the chain. The `collect` stays where your coroutine is.

## StateFlow - a Flow that remembers

A plain Flow does not remember the last value. If you start collecting late, you missed everything.

`StateFlow` always holds the **current value**, and gives it to anyone who starts collecting.

```kotlin
class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val user = fetchUser()
            _uiState.value = UiState.Success(user)
        }
    }
}
```

**Analogy:**

- **Flow** = a live radio broadcast. Tune in late, you missed the song.
- **StateFlow** = a scoreboard. Whenever you look at it, the current score is right there.

This is why `StateFlow` is the standard way to hold UI state in a ViewModel. Rotate the phone, the screen re-collects, and the current state is still there.

## Quick comparison

| | Gives | Cold or hot | Remembers last value |
|---|---|---|---|
| `suspend fun` | One value | - | - |
| `Flow` | Many values | Cold - starts on `collect` | No |
| `StateFlow` | Many values | Hot - always alive | Yes |

## Thumb-rules

- One value → suspend function. Many values over time → Flow.
- `emit()` puts values in. `collect { }` takes them out.
- A Flow is cold - nothing runs until you collect.
- `collect` is a suspend function, so it needs a coroutine.
- `flowOn` picks the thread for the emitting side.
- UI state in a ViewModel → `StateFlow`.

## You made it

Ten parts. Look back at what you now understand:

1. Coroutines are functions cooperating.
2. They exist because callbacks and frozen UIs hurt.
3. `suspend` means pause and resume, not block.
4. Dispatchers pick the thread.
5. `launch` fires and forgets, `async` returns a result.
6. `withContext` shifts context without a new coroutine.
7. Scopes decide when coroutines die.
8. `CoroutineContext` is the backpack they carry.
9. Failures are handled with `try-catch`, handlers, and the right scope.
10. Flow handles many values over time.

Now go write some code. Re-reading is not learning - typing is.

That's it for now.
