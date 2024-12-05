package com.example.timeflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.timeflex.data.User
import com.example.timeflex.repository.UserRepository
import com.example.timeflex.ui.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreateAccountScreen(navController: NavController) {
    val firebaseAuth = FirebaseAuth.getInstance()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var organizationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userRepository = UserRepository()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create An Account!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(20.dp))

                // First Name Input
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Last Name Input
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password Input
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Organization Code Input
                OutlinedTextField(
                    value = organizationCode,
                    onValueChange = { organizationCode = it },
                    label = { Text("Organization Code") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Create Account Button
                Button(
                    onClick = {
                        isLoading = true
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val firebaseUser = authResult.user
                                if (firebaseUser != null) {
                                    val newUser = User(
                                        id = firebaseUser.uid,
                                        firstName = firstName,
                                        lastName = lastName,
                                        email = firebaseUser.email ?: email,
                                        organizationCode = organizationCode
                                    )

                                    userRepository.addUser(
                                        user = newUser,
                                        onSuccess = {
                                            isLoading = false
                                            navController.navigate(Routes.LOGIN) {
                                                popUpTo(Routes.LOGIN) { inclusive = true }
                                            }
                                        },
                                        onFailure = { exception ->
                                            isLoading = false
                                            errorMessage = "Failed to save user data: ${exception.localizedMessage}"
                                        }
                                    )
                                } else {
                                    isLoading = false
                                    errorMessage = "Failed to retrieve user details after account creation."
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                errorMessage = "Account creation failed: ${exception.localizedMessage}"
                            }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading &&
                            firstName.isNotBlank() &&
                            lastName.isNotBlank() &&
                            email.isNotBlank() &&
                            password.isNotBlank() &&
                            confirmPassword.isNotBlank() &&
                            organizationCode.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Account")
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))

                // Back to Login Button
                TextButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Login")
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Error Message
                if (!errorMessage.isNullOrEmpty()) {
                    LaunchedEffect(errorMessage) {
                        scope.launch {
                            snackbarHostState.showSnackbar(message = errorMessage!!)
                        }
                    }
                }
            }
        }
    )
}