package com.fahim.learncoroutinesbytutorials.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelper
import com.fahim.learncoroutinesbytutorials.data.local.DatabaseHelper
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.exceptionhandler.ExceptionHandlerViewModel
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.supervisor.IgnoreErrorAndContinueViewModel
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.trycatch.TryCatchViewModel
import com.fahim.learncoroutinesbytutorials.ui.retrofit.parallel.ParallelNetworkCallsViewModel
import com.fahim.learncoroutinesbytutorials.ui.retrofit.series.SeriesNetworkCallsViewModel
import com.fahim.learncoroutinesbytutorials.ui.retrofit.single.SingleNetworkCallViewModel
import com.fahim.learncoroutinesbytutorials.ui.room.RoomDBViewModel
import com.fahim.learncoroutinesbytutorials.ui.task.onetask.LongRunningTaskViewModel
import com.fahim.learncoroutinesbytutorials.ui.task.twotasks.TwoLongRunningTasksViewModel
import com.fahim.learncoroutinesbytutorials.ui.timeout.TimeoutViewModel

/**
 * Hand-rolled factory (no DI framework) so every example ViewModel receives
 * the same [ApiHelper] and [DatabaseHelper] instances.
 */
class ViewModelFactory(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(SingleNetworkCallViewModel::class.java) ->
                SingleNetworkCallViewModel(apiHelper)

            modelClass.isAssignableFrom(SeriesNetworkCallsViewModel::class.java) ->
                SeriesNetworkCallsViewModel(apiHelper)

            modelClass.isAssignableFrom(ParallelNetworkCallsViewModel::class.java) ->
                ParallelNetworkCallsViewModel(apiHelper)

            modelClass.isAssignableFrom(RoomDBViewModel::class.java) ->
                RoomDBViewModel(apiHelper, dbHelper)

            modelClass.isAssignableFrom(LongRunningTaskViewModel::class.java) ->
                LongRunningTaskViewModel()

            modelClass.isAssignableFrom(TwoLongRunningTasksViewModel::class.java) ->
                TwoLongRunningTasksViewModel()

            modelClass.isAssignableFrom(TimeoutViewModel::class.java) ->
                TimeoutViewModel(apiHelper)

            modelClass.isAssignableFrom(TryCatchViewModel::class.java) ->
                TryCatchViewModel(apiHelper)

            modelClass.isAssignableFrom(ExceptionHandlerViewModel::class.java) ->
                ExceptionHandlerViewModel(apiHelper)

            modelClass.isAssignableFrom(IgnoreErrorAndContinueViewModel::class.java) ->
                IgnoreErrorAndContinueViewModel(apiHelper)

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        return viewModel as T
    }
}
