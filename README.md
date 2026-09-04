# Master Coroutines - The Simple Way

Kotlin Coroutines explained in small parts, with everyday analogies.

No prior knowledge of threads, RxJava, or concurrency needed.

Read them in order. Each part is short. Each part builds on the one before it.

## Parts

| # | Part | What you learn |
|---|------|----------------|
| 1 | [What are Coroutines?](01-what-are-coroutines.md) | The idea behind coroutines, in plain words |
| 2 | [Why do we need Coroutines?](02-why-do-we-need-coroutines.md) | The problem they solve - frozen apps and callback hell |
| 3 | [Suspend Function](03-suspend-function.md) | Pause and resume, without blocking |
| 4 | [Dispatchers](04-dispatchers.md) | Choosing the right thread for the right work |
| 5 | [Launch vs Async](05-launch-vs-async.md) | Fire and forget vs get a result back |
| 6 | [withContext](06-withcontext.md) | Switching threads without starting a new coroutine |
| 7 | [Scopes](07-scopes.md) | Making tasks stop when the screen is gone |
| 8 | [CoroutineContext](08-coroutinecontext.md) | The settings a coroutine carries with it |
| 9 | [Exception Handling](09-exception-handling.md) | What happens when things fail |
| 10 | [Flow - The Basics](10-flow-basics.md) | Many values over time, not just one |

## How to read this

- Read one part a day. Do not rush.
- Type the code yourself. Do not copy-paste.
- If a part feels heavy, go back one part. The gap is usually there.

## Coming later

- Deeper technical internals (state machines, continuations, `Job` tree)
- Real Android examples (Retrofit, Room, ViewModel)
- Quizzes and practice exercises at the end of every part

That's it for now.
