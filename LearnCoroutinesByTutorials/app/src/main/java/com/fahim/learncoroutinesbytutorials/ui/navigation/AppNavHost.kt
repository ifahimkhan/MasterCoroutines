package com.fahim.learncoroutinesbytutorials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fahim.learncoroutinesbytutorials.ui.base.ViewModelFactory
import com.fahim.learncoroutinesbytutorials.ui.basic.BasicScreen
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.exceptionhandler.ExceptionHandlerScreen
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.supervisor.IgnoreErrorAndContinueScreen
import com.fahim.learncoroutinesbytutorials.ui.errorhandling.trycatch.TryCatchScreen
import com.fahim.learncoroutinesbytutorials.ui.home.HomeScreen
import com.fahim.learncoroutinesbytutorials.ui.retrofit.parallel.ParallelNetworkCallsScreen
import com.fahim.learncoroutinesbytutorials.ui.retrofit.series.SeriesNetworkCallsScreen
import com.fahim.learncoroutinesbytutorials.ui.retrofit.single.SingleNetworkCallScreen
import com.fahim.learncoroutinesbytutorials.ui.room.RoomDBScreen
import com.fahim.learncoroutinesbytutorials.ui.task.onetask.LongRunningTaskScreen
import com.fahim.learncoroutinesbytutorials.ui.task.twotasks.TwoLongRunningTasksScreen
import com.fahim.learncoroutinesbytutorials.ui.timeout.TimeoutScreen

@Composable
fun AppNavHost(
    factory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val onBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onExampleClick = { screen -> navController.navigate(screen.route) })
        }
        composable(Screen.SingleNetworkCall.route) {
            SingleNetworkCallScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.SeriesNetworkCalls.route) {
            SeriesNetworkCallsScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.ParallelNetworkCalls.route) {
            ParallelNetworkCallsScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.RoomDB.route) {
            RoomDBScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.LongRunningTask.route) {
            LongRunningTaskScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.TwoLongRunningTasks.route) {
            TwoLongRunningTasksScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.Timeout.route) {
            TimeoutScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.TryCatch.route) {
            TryCatchScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.ExceptionHandler.route) {
            ExceptionHandlerScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.IgnoreErrorAndContinue.route) {
            IgnoreErrorAndContinueScreen(factory = factory, onBack = onBack)
        }
        composable(Screen.Basic.route) {
            BasicScreen(onBack = onBack)
        }
    }
}
