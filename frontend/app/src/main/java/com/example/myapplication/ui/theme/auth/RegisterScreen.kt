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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.myapplication.model.auth.RegisterUserInfo
import com.example.myapplication.model.response.ArtistDetailInfo
import com.example.myapplication.showSnackbar
import com.example.myapplication.ui.theme.MyApplicationTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(authViewModel: AuthViewModel, navController: NavController) {
    val isLoadingRegister = remember { mutableStateOf(false) }

    var fullname by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var backendError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text("Register") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    )

    var fullnameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var emailErrorText by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }


    fun validateEmail(email: String) {
        if (email == "") {
            emailErrorText = "Email cannot be empty"
            emailError = true
        } else {
            emailError = false
            emailErrorText = ""
        }

    }
    fun validateFullname(fullname: String) {
        fullnameError = fullname.isBlank()
    }

    fun validatePassword(password: String) {
        passwordError = password == ""

    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        OutlinedTextField(
            value = fullname,
            onValueChange = { value ->
                fullname = value
                validateFullname(fullname)
            },
            isError = fullnameError,
            label = {Text(text="Enter full name")},
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            .onFocusChanged { focusState ->
            if (focusState.isFocused) {
                validateFullname(fullname)
            }
        },
            supportingText = {
                if (fullnameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth()
                        .offset(x = (-16).dp),
                        text = "Full name cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                    )
                }
            }
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validateEmail(email)
            },

            isError = emailError,
            label = {Text(text="Enter email")},
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    validateEmail(email)
                }},
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
            label = {Text(text="Password")},
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        validatePassword(password)
                    }},
            supportingText = {
                if (passwordError) {
                    Text(
                        modifier = Modifier.fillMaxWidth()
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
                // check if email pattern is correct or not
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailErrorText = "Invalid email format"
                    emailError = true
                } else {
                    emailError = false
                    emailErrorText = ""
                    isLoadingRegister.value = false

                    if (!fullnameError && !emailError && !passwordError) {
                        isLoadingRegister.value = true
                        val userInfo = RegisterUserInfo(
                            fullName = fullname,
                            emailAddress = email,
                            password = password,
                        )
                        authViewModel.registerUser(userInfo) { response ->
                            isLoadingRegister.value = false
                            if (response != null) {
                                if (response.message == "User registered successfully") {
                                    showSnackbar(scope, "Registered successfully")
                                    authViewModel.checkAuth()
                                    navController.navigate(Routes.homeScreen) {
                                        popUpTo(Routes.loginScreen) { inclusive = true }
                                    }
                                } else {
                                    backendError = response.message
                                    println(backendError)
                                    if (backendError.contains("email already exists")) {
                                        emailError = true
                                        emailErrorText = "Email already exists"
                                    }
                                    println("Login or Register failed: ${response.message}")
                                }
                            }
                        }
                    }
                }

            },
            enabled = !isLoadingRegister.value,
            modifier = Modifier.fillMaxWidth()
                .height(50.dp).padding(top = 10.dp)
        ) {
            if (isLoadingRegister.value) {
                CircularProgressIndicator( modifier = Modifier.size(25.dp),
                    color = Color.White)
            } else {
                Text("Register")
            }
        }

        Row() {

            Text("Already have an account? ")
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable{
                    navController.navigate(Routes.loginScreen)
                }
            )
        }

    }





}




@Preview(showBackground = true)
@Composable
fun ShowRegisterScreen() {
    MyApplicationTheme {
//        RegisterScreen(navController = )
    }
}
