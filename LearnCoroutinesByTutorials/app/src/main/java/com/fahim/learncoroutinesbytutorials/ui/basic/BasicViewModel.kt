package com.fahim.learncoroutinesbytutorials.ui.basic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * Port of the "BasicActivity" experiments to a ViewModel + Compose.
 *
 * `viewModelScope` plays the role `lifecycleScope` played in the Activity.
 * Every experiment appends to an on-screen log (and Logcat) so the order of
 * execution and the thread each line runs on are visible.
 */
class BasicViewModel : ViewModel() {

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    /** Custom scope, cancelled in [onCleared], like a hand-made lifecycleScope. */
    private val myScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    /** Single-threaded dispatcher used to compare suspending vs blocking. */
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        log("exception handler: $e")
    }

    val examples: List<BasicExample> = listOf(
        BasicExample("launch", "lifecycle-like scope, default dispatcher", ::testCoroutine),
        BasicExample("launch(Main)", "Dispatchers.Main", ::testCoroutineWithMain),
        BasicExample("launch(Main.immediate)", "Runs body immediately if already on Main", ::testCoroutineWithMainImmediate),
        BasicExample("Two launches on Main", "Both start, both suspend, both resume", ::testCoroutineEverything),
        BasicExample("GlobalScope (never do this)", "Lives forever, ignores lifecycle", ::usingGlobalScope),
        BasicExample("GlobalScope inside scope", "Child is NOT a child: parent does not wait", ::globalScopeInsideLifecycleScope),
        BasicExample("launch inside launch", "Real child: structured concurrency", ::launchInsideLifecycleScope),
        BasicExample("Two launches (Default)", "Run in parallel on background threads", ::twoLaunches),
        BasicExample("Two async + await", "Parallel, combined result", ::twoAsyncInsideLifecycleScope),
        BasicExample("Two withContext", "Sequential, combined result", ::twoWithContextInsideLifecycleScope),
        BasicExample("Custom scope", "Scope cancelled in onCleared()", ::usingMyActivityScope),
        BasicExample("Cancel job from another", "job.cancel() stops task 1", ::twoTasks),
        BasicExample("Child cancels parent", "delay() throws after cancel", ::parentAndChildTaskCancel),
        BasicExample("Child cancels parent + isActive", "Check isActive before continuing", ::parentAndChildTaskCancelIsActive),
        BasicExample("Handler + exception", "CoroutineExceptionHandler catches it", ::lifecycleScopeWithHandlerException),
        BasicExample("Handler, no exception", "Handler unused, task completes", ::lifecycleScopeWithHandler),
        BasicExample("Custom scope + handler + exception", "Same, on custom scope", ::myActivityScopeWithHandlerException),
        BasicExample("Custom scope + handler", "Same, no exception", ::myActivityScopeWithHandler),
        BasicExample("Exception in launch (CRASHES APP)", "Uncaught: propagates to scope", ::exceptionInLaunchBlock),
        BasicExample("Exception in async (no await)", "Stored in Deferred, nothing happens", ::exceptionInAsyncBlock),
        BasicExample("Exception in async + await", "Thrown at await(), caught by try-catch", ::exceptionInAsyncBlockWithAwait),
        BasicExample("Suspending", "Two tasks on 1 thread: run in parallel", ::testSuspending),
        BasicExample("Blocking", "runBlocking on 1 thread: run one after another", ::testBlocking)
    )

    fun clearLogs() {
        _logs.value = emptyList()
    }

    private fun log(message: String) {
        val line = "[${Thread.currentThread().name}] $message"
        Log.d(TAG, line)
        _logs.update { it + line }
    }

    // ---------------------------------------------------------------- basics

    private fun testCoroutine() {
        log("Function Start")
        viewModelScope.launch {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private fun testCoroutineWithMain() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    // Similar to testCoroutine: viewModelScope already uses Main.immediate
    private fun testCoroutineWithMainImmediate() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Main.immediate) {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private fun testCoroutineEverything() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task 1")
            doLongRunningTask()
            log("After Task 1")
        }
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task 2")
            doLongRunningTask()
            log("After Task 2")
        }
        log("Function End")
    }

    // NOTE: NEVER USE GlobalScope. Used here for educational purposes only.
    @OptIn(DelicateCoroutinesApi::class)
    private fun usingGlobalScope() {
        log("Function Start")
        GlobalScope.launch {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private fun usingMyActivityScope() {
        log("Function Start")
        myScope.launch {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            log("Before Delay")
            delay(LONG_TASK_MS)
            log("After Delay")
        }
    }

    private suspend fun doLongRunningTaskOne(): Int {
        return withContext(Dispatchers.Default) {
            delay(LONG_TASK_MS)
            return@withContext TASK_RESULT
        }
    }

    private suspend fun doLongRunningTaskTwo(): Int {
        return withContext(Dispatchers.Default) {
            delay(LONG_TASK_MS)
            return@withContext TASK_RESULT
        }
    }

    // ------------------------------------------------------- parent / child

    @OptIn(DelicateCoroutinesApi::class)
    private fun globalScopeInsideLifecycleScope() {
        log("Function Start")
        viewModelScope.launch {
            log("Before Task")
            GlobalScope.launch(Dispatchers.Default) {
                log("Before Delay")
                delay(LONG_TASK_MS)
                log("After Delay")
            }
            log("After Task")
        }
        log("Function End")
    }

    private fun launchInsideLifecycleScope() {
        log("Function Start")
        viewModelScope.launch {
            log("Before Task")
            launch(Dispatchers.Default) {
                log("Before Delay")
                delay(LONG_TASK_MS)
                log("After Delay")
            }
            log("After Task")
        }
        log("Function End")
    }

    private fun twoLaunches() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Default) {
            log("Before Delay 1")
            delay(LONG_TASK_MS)
            log("After Delay 1")
        }
        viewModelScope.launch(Dispatchers.Default) {
            log("Before Delay 2")
            delay(LONG_TASK_MS)
            log("After Delay 2")
        }
        log("Function End")
    }

    private fun twoWithContextInsideLifecycleScope() {
        log("Function Start")
        viewModelScope.launch {
            log("Before Task 1")
            val resultOne = doLongRunningTaskOne()
            log("After Task 1")
            log("Before Task 2")
            val resultTwo = doLongRunningTaskTwo()
            log("After Task 2")
            log("result : ${resultOne + resultTwo}")
        }
        log("Function End")
    }

    private fun twoAsyncInsideLifecycleScope() {
        log("Function Start")
        viewModelScope.launch {
            log("Before Task")
            val deferredOne = async { doLongRunningTaskOne() }
            val deferredTwo = async { doLongRunningTaskTwo() }
            val result = deferredOne.await() + deferredTwo.await()
            log("result : $result")
            log("After Task")
        }
        log("Function End")
    }

    // ---------------------------------------------------------- cancellation

    private fun twoTasks() {
        log("Function Start")
        val job = viewModelScope.launch(Dispatchers.Main) {
            log("Before Task 1")
            doLongRunningTask()
            log("After Task 1")
        }
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task 2")
            job.cancel()
            doLongRunningTask()
            log("After Task 2")
        }
        log("Function End")
    }

    private fun parentAndChildTaskCancel() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task")
            childTask(coroutineContext[Job]!!)
            log("After Task")
        }
        log("Function End")
    }

    private suspend fun childTask(parent: Job) {
        withContext(Dispatchers.Default) {
            log("childTask start")
            parent.cancel()
            log("childTask parent cancel")
            // delay() is a suspension point: it checks for cancellation and throws
            delay(LONG_TASK_MS)
            log("childTask end")
        }
    }

    private fun parentAndChildTaskCancelIsActive() {
        log("Function Start")
        viewModelScope.launch(Dispatchers.Main) {
            log("Before Task")
            childTaskWithIsActive(coroutineContext[Job]!!)
            log("After Task")
        }
        log("Function End")
    }

    private suspend fun childTaskWithIsActive(parent: Job) {
        withContext(Dispatchers.Default) {
            log("childTask start")
            parent.cancel()
            if (isActive) {
                log("childTask parent cancel")
            } else {
                log("childTask is NOT active")
            }
            delay(LONG_TASK_MS)
            log("childTask end")
        }
    }

    // ------------------------------------------------------------ exceptions

    private fun lifecycleScopeWithHandlerException() {
        log("Function Start")
        viewModelScope.launch(exceptionHandler) {
            log("Before Task")
            doLongRunningTask()
            throw Exception("Some Exception")
        }
        log("Function End")
    }

    private fun lifecycleScopeWithHandler() {
        log("Function Start")
        viewModelScope.launch(exceptionHandler) {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private fun myActivityScopeWithHandlerException() {
        log("Function Start")
        myScope.launch(exceptionHandler) {
            log("Before Task")
            doLongRunningTask()
            throw Exception("Some Exception")
        }
        log("Function End")
    }

    private fun myActivityScopeWithHandler() {
        log("Function Start")
        myScope.launch(exceptionHandler) {
            log("Before Task")
            doLongRunningTask()
            log("After Task")
        }
        log("Function End")
    }

    private fun exceptionInLaunchBlock() {
        log("launch without handler: app will crash")
        viewModelScope.launch {
            doSomethingAndThrowException()
        }
    }

    private fun exceptionInAsyncBlock() {
        log("async without await: exception stays inside Deferred")
        viewModelScope.async {
            doSomethingAndThrowException()
        }
    }

    private fun exceptionInAsyncBlockWithAwait() {
        viewModelScope.launch {
            val deferred = viewModelScope.async(Dispatchers.Default) {
                doSomethingAndThrowException()
                return@async TASK_RESULT
            }
            try {
                val result = deferred.await()
                log("result : $result")
            } catch (e: Exception) {
                log("exception handler: $e")
            }
        }
    }

    private fun doSomethingAndThrowException() {
        throw Exception("Some Exception")
    }

    // ------------------------------------------------- suspending vs blocking

    private suspend fun timeTakingTask() {
        withContext(Dispatchers.IO) {
            // Thread.sleep blocks the thread, unlike delay()
            Thread.sleep(BLOCKING_TASK_MS)
        }
    }

    private fun testSuspending() {
        viewModelScope.launch(singleThreadDispatcher) {
            log("testSuspending Before Task 1")
            timeTakingTask()
            log("testSuspending After Task 1")
        }
        viewModelScope.launch(singleThreadDispatcher) {
            log("testSuspending Before Task 2")
            timeTakingTask()
            log("testSuspending After Task 2")
        }
    }

    private fun testBlocking() {
        viewModelScope.launch(singleThreadDispatcher) {
            runBlocking {
                log("testBlocking Before Task 1")
                timeTakingTask()
                log("testBlocking After Task 1")
            }
        }
        viewModelScope.launch(singleThreadDispatcher) {
            runBlocking {
                log("testBlocking Before Task 2")
                timeTakingTask()
                log("testBlocking After Task 2")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        myScope.cancel()
        singleThreadDispatcher.close()
    }

    companion object {
        private const val TAG = "BasicViewModel"
        private const val LONG_TASK_MS = 2000L
        private const val BLOCKING_TASK_MS = 5000L
        private const val TASK_RESULT = 10
    }
}
