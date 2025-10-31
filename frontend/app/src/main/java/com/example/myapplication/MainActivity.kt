package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycling
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.auth.AuthViewModel
import com.example.myapplication.ui.theme.auth.LoginScreen
import com.example.myapplication.ui.theme.auth.RegisterScreen


import com.example.myapplication.ui.theme.home.HomeScreen
import com.example.myapplication.ui.theme.search.ArtistDetailScreen
import com.example.myapplication.ui.theme.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        authViewModel.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {

                val isLoggedIn by authViewModel.isLoggedIn
                val userData by authViewModel.currentUser

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                TrackEvents(flow = SnackBarController.events) { event ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = event.text,
                            duration = SnackbarDuration.Short
                        )
                    }

                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                        )
                    },

                    modifier = Modifier.fillMaxSize(),
                    // Disable automatic insets handling
                    contentWindowInsets = WindowInsets(0.dp)
                ) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Routes.homeScreen,

//                        modifier = Modifier.fillMaxSize()
                        modifier = Modifier.padding(innerPadding)
//                            .padding(top = statusBarPadding.calculateTopPadding())

                    ) {

                        composable(Routes.homeScreen) {
                            HomeScreen(
                                authViewModel = authViewModel,
                                searchViewModel = searchViewModel,
                                navController = navController,
                                isLoggedIn,
                                userData
                            )
                        }
                        if (!isLoggedIn) {
                            composable(Routes.loginScreen) {
                                LoginScreen(authViewModel, navController)
                            }

                            composable(Routes.registerScreen) {
                                RegisterScreen(authViewModel, navController)
                            }
                        }



                        composable(
                            route =
                                "${Routes.ArtistDetailScreen}/{artistId}"
                        ) { navBackStackEntry ->
                            // Retrieve the passed argument
                            val artistId =
                                navBackStackEntry.arguments?.getString("artistId")

                            // Pass accountType to SingleAccountScreen
                            ArtistDetailScreen(
                                authViewModel,
                                isLoggedIn = isLoggedIn,
                                artistId = artistId.toString(),
                                navController = navController
                            )
                        }
                    }
                }
            }

//    HomeScreenBackUp()


//                TopBar()
//            SearchScreen()



        }

        }
    }



data class SnackBar(
    val text: String,
//    val action: () -> Unit
)

//data class SnackBarAction(
//    val text: String,
//    val action: () -> Unit
//)

object SnackBarController {
    private val _events = Channel<SnackBar>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackBar) {
        _events.send(event)
    }

}

fun showSnackbar(scope: CoroutineScope, message: String) {
    scope.launch {

        println("Snackbar should show: $message")
        SnackBarController.sendEvent(SnackBar(message))
    }
}




@Composable
fun <T>TrackEvents(flow: Flow<T>,
                   key: Any? = null,
                   onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }

}


fun showNotification(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
//        Greeting("Android")

    }
}