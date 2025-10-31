package com.example.myapplication.ui.theme.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.Routes
import com.example.myapplication.SnackBar
import com.example.myapplication.SnackBarController
import com.example.myapplication.model.auth.LoginUserInfo
import com.example.myapplication.model.auth.RegisterUserInfo
import com.example.myapplication.showNotification
import com.example.myapplication.showSnackbar
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.search.SearchViewModel
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController) {
    val isLoadingRegister = remember { mutableStateOf(false) }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }
    var backendError by remember { mutableStateOf(false) }
    var backendErrorText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
//    val context = LocalContext.current

    fun validateEmail(email: String) {
        if (email == "") {
            emailErrorText = "Email cannot be empty"
            emailError = true
        } else {
            emailError = false
            emailErrorText = ""
        }

    }

    fun validatePassword(password: String) {
        passwordError = password == ""

    }

    TopAppBar(
        title = { Text("Login") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validateEmail(email)
            },
            isError = emailError,
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        validateEmail(email)
                    }
                },
            supportingText = {
                if (emailError) {
                    Text(
                        modifier = Modifier
                            .offset(x = (-16).dp),
                        text = emailErrorText,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                    )
                }
            },
        )


        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                validatePassword(password)
            },

            label = { Text(text = "Password") },
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        validatePassword(password)
                    }
                },
            supportingText = {
                if (passwordError) {
                    Text(
                        modifier = Modifier
                            .offset(x = (-16).dp),
                        text = "Password cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                    )
                }
            },
        )

        Button(

            onClick = {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailErrorText = "Invalid email format"
                    emailError = true
                } else {
                    emailError = false
                    emailErrorText = ""
                    isLoadingRegister.value = false

                    if (!emailError && !passwordError) {
                        isLoadingRegister.value = true
                        val userInfo = LoginUserInfo(
                            emailAddress = email,
                            password = password,
                        )
                        authViewModel.loginUser(userInfo) { response ->
                            isLoadingRegister.value = false
                            if (response != null) {
                                println("Login backend response: " + response.message)
                                if (response.message == "Authentication success") {
                                    authViewModel.checkAuth { res ->
                                        if (res != null) {
                                            showSnackbar(scope, "Logged in successfully")
//                                            showNotification(context, "Logged in successfully")

                                            navController.navigate(Routes.homeScreen) {
                                                popUpTo(Routes.loginScreen) { inclusive = true }
                                            }
                                        }
                                    }

                                } else {
                                    println(backendError)
                                    if (response.message.contains("Password or email is incorrect")) {
                                        backendErrorText = "Username or Password is incorrect"
                                        backendError = true
                                    } else {
                                        backendError = false
                                        backendErrorText = ""
                                    }
                                }
                            }
                        }
                    }
                }

            },
            enabled = !isLoadingRegister.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp).padding(top = 10.dp)
        ) {
            if (isLoadingRegister.value) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(25.dp) )
            } else {
                Text("Login")
            }
        }
        if (backendError) {
            Text(
                text = backendErrorText,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Start).padding(vertical = 5.dp)
            )
        }

        Row(modifier = Modifier.padding(vertical = 5.dp)) {
            Text("Don't have an account yet? ")
            Text(
                text = "Register",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate(Routes.registerScreen)
                }
            )
        }


    }


}

fun onClickLogin() {
    TODO("Not yet implemented")
}


@Preview(showBackground = true)
@Composable
fun ShowLoginScreen() {
    MyApplicationTheme {
//        LoginScreen()
    }
}