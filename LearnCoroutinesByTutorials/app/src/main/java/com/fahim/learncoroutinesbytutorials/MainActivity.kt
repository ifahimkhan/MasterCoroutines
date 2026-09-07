package com.fahim.learncoroutinesbytutorials

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fahim.learncoroutinesbytutorials.data.api.ApiHelperImpl
import com.fahim.learncoroutinesbytutorials.data.api.RetrofitBuilder
import com.fahim.learncoroutinesbytutorials.data.local.DatabaseBuilder
import com.fahim.learncoroutinesbytutorials.data.local.DatabaseHelperImpl
import com.fahim.learncoroutinesbytutorials.ui.base.ViewModelFactory
import com.fahim.learncoroutinesbytutorials.ui.navigation.AppNavHost
import com.fahim.learncoroutinesbytutorials.ui.theme.LearnCoroutinesByTutorialsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = ViewModelFactory(
            apiHelper = ApiHelperImpl(RetrofitBuilder.apiService),
            dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
        )

        setContent {
            LearnCoroutinesByTutorialsTheme {
                AppNavHost(factory = factory)
            }
        }
    }
}
