# Part 7: Scopes

In every part so far, I kept writing the same note:

> **Note:** avoid `GlobalScope`. Use `lifecycleScope` or `viewModelScope`.

Now let's finally understand why.

## The problem

A user opens a screen. A network call starts. It takes 3 seconds.

After 1 second, the user presses back. The screen is destroyed.

But the network call is **still running**.

When it finishes, it tries to update a screen that no longer exists.

Result: wasted battery, wasted data, leaked memory, and sometimes a crash.

## The analogy

A **scope** is a **contract with an end date**.

Think of hiring workers for a construction site.

- **`GlobalScope`** = the worker is hired **forever**. Even after the building is demolished, he keeps showing up and hammering the air. Nobody ever told him to stop.
- **A proper scope** = the worker is hired **for this building only**. Building gone → contract over → worker goes home automatically.

That automatic "go home" is the whole point of scopes.

## What is a scope, technically?

> A scope defines the **lifetime** of the coroutines started inside it.

When the scope is cancelled, **every coroutine inside it is cancelled too**.

You do not have to track them one by one. That is the gift.

## Activity - use lifecycleScope

The Activity is the scope. When the Activity is destroyed, the work should stop.

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val user = fetchUser()
            // show user
        }
    }

    suspend fun fetchUser(): User {
        return withContext(Dispatchers.IO) {
            // fetch user
            // return user
        }
    }
}
```

As soon as the Activity is destroyed, the task gets cancelled if it is still running - because we used a scope bound to the **lifecycle of the Activity**.

## ViewModel - use viewModelScope

The ViewModel is the scope. When the ViewModel is cleared, the work should stop.

```kotlin
class MainViewModel : ViewModel() {

    fun fetch() {
        viewModelScope.launch {
            val user = fetchUser()
            // show user
        }
    }

    suspend fun fetchUser(): User {
        return withContext(Dispatchers.IO) {
            // fetch user
            // return user
        }
    }
}
```

Same idea, different owner.

**Which one should you use?**

Mostly `viewModelScope`. A ViewModel survives screen rotation, an Activity does not. So work started in `viewModelScope` is not thrown away when the user rotates the phone.

Use `lifecycleScope` for work that is truly tied to the UI itself.

> These scopes come from the Android KTX extension libraries. Make sure the required dependencies are added to your project.

## Why GlobalScope is dangerous

```kotlin
GlobalScope.launch {
    // this runs until the whole app process dies
}
```

`GlobalScope` is tied to the **application**, not to any screen.

- Nothing cancels it when the screen closes.
- It can hold references to a dead Activity → memory leak.
- In tests, it is very hard to control.

I used it in earlier parts only because it is short and keeps examples readable. In real code, do not.

## Two scope helpers you should know

These are not "where to launch", they are "how to group work inside a coroutine".

**`coroutineScope { }`** - if any child fails, cancel all the others.

**`supervisorScope { }`** - if a child fails, the other children keep going.

**Analogy:** a group project.

- `coroutineScope` = *"if one member fails, the whole submission is cancelled."*
- `supervisorScope` = *"if one member fails, the rest still submit their parts."*

We will use both in Part 9.

## Thumb-rules

- A scope decides **when the coroutine dies**.
- Cancel the scope → every coroutine inside it is cancelled.
- Activity → `lifecycleScope`. ViewModel → `viewModelScope`.
- Prefer `viewModelScope` - it survives rotation.
- Avoid `GlobalScope` in real projects.

Next: [CoroutineContext](08-coroutinecontext.md)

That's it for now.
