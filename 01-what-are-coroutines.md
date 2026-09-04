# Part 1: What are Coroutines?

Let's get started.

## Break the word first

```
Coroutines = Co + Routines
```

Here, **Co** means **cooperation** and **Routines** means **functions**.

So when functions cooperate with each other, we call it Coroutines.

That's the whole idea. Everything else is detail.

## The restaurant analogy

Imagine a restaurant with one waiter.

A customer orders food. The waiter gives the order to the kitchen.

Now, what should the waiter do?

**Option 1:** Stand near the kitchen and wait for the food.

The waiter is doing nothing for 10 minutes. Other customers wait. They get angry. This is a **blocked thread**.

**Option 2:** Give the order to the kitchen, then go take orders from other tables. When the food is ready, come back and serve it.

Same one waiter. Same one kitchen. But nobody is standing idle.

**Option 2 is a coroutine.**

The waiter did not stop. The waiter **paused** one job and picked up another job, then came back later.

## Cooperation, in code

Let's see the same idea with two functions, `functionA` and `functionB`.

```kotlin
fun functionA() {
    taskA1()
    // now let functionB do some work
    functionB()
    // and come back here to continue
    taskA2()
}

fun functionB() {
    taskB1()
    taskB2()
}
```

Here, `functionA` does `taskA1`, then hands over control to `functionB`.

`functionB` does its work and gives control back to `functionA`.

They are **cooperating**.

With Kotlin Coroutines, this handing over of control happens automatically. We do not write it by hand.

## Coroutines vs Threads

Both do many things at once. So what is different?

Think of it like this:

- A **thread** is a full worker hired by the operating system. Hiring one is expensive. Keeping 1000 of them is very expensive.
- A **coroutine** is a **task on a sticky note**. A worker picks up a note, does some of it, sticks it back, picks another note.

We can create thousands of sticky notes. They cost almost nothing.

This is why we say:

> Coroutines are lightweight threads.

**Lightweight** means a coroutine does not map to a real OS thread of its own. So the processor does not have to do context switching for it. That makes it fast and cheap.

## Stackless vs Stackful

Coroutines exist in many languages. There are two types:

- **Stackless** - the coroutine does not carry its own stack.
- **Stackful** - the coroutine carries its own stack.

Kotlin implements **stackless** coroutines. That is exactly why they don't map onto a native thread.

You do not need to remember this on day one. Just know the word if someone asks in an interview.

## What the official Kotlin docs say

Now this paragraph should make sense to you:

> One can think of a coroutine as a light-weight thread. Like threads, coroutines can run in parallel, wait for each other and communicate. The biggest difference is that coroutines are very cheap, almost free: we can create thousands of them, and pay very little in terms of performance. True threads, on the other hand, are expensive to start and keep around.

## One important line

**Coroutines do not replace threads.**

Threads are still there underneath. Coroutines are a **framework to manage them** in a smarter way.

## The exact definition

> A framework to manage concurrency in a more performant and simple way with its lightweight thread, which is written on top of the actual threading framework, to get the most out of it by taking advantage of the cooperative nature of functions.

Read that once now. Read it again after Part 5. It will feel much lighter then.

## Thumb-rules

- Coroutines = functions cooperating with each other.
- A coroutine can pause and resume. A blocked thread cannot.
- Coroutines are cheap. Threads are expensive.
- Coroutines run on top of threads, they do not remove them.

Next: [Why do we need Coroutines?](02-why-do-we-need-coroutines.md)

That's it for now.
