package com.example.jsync.presentation.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80


@Composable
fun AuthScreen(viewModel : AuthScreenViewModel , navigateToHome : () -> Unit) {
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    var signUpState by rememberSaveable { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    var nameInputColor by remember { mutableStateOf(Color.Gray) }
    var emailInputColor by remember { mutableStateOf(Color.Gray) }
    var passInputColor by remember { mutableStateOf(Color.Gray) }

    var nameInputInfo by remember { mutableStateOf("") }
    var emailInputInfo by remember { mutableStateOf("") }
    var passInputInfo by remember { mutableStateOf("") }
    val context = LocalContext.current
    var enabled by rememberSaveable { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = if (signUpState) "Sign Up" else "Sign In", fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(signUpState) {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your name") },
                    shape = CircleShape,
                    value = name,
                    onValueChange = {
                        name = it
                        nameInputColor = Color.Gray
                        nameInputInfo = ""
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "name")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = nameInputColor,
                        unfocusedIndicatorColor = nameInputColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                AnimatedVisibility(visible = nameInputInfo.isNotEmpty()) {
                    Text(text = nameInputInfo, color = Color.Red, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email field
        Column {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your email") },
                shape = CircleShape,
                value = email,
                onValueChange = {
                    email = it
                    emailInputColor = Color.Gray
                    emailInputInfo = ""
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "email")
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = emailInputColor,
                    unfocusedIndicatorColor = emailInputColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            AnimatedVisibility(visible = emailInputInfo.isNotEmpty()) {
                Text(text = emailInputInfo, color = Color.Red, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Password field
        Column {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your password") },
                shape = CircleShape,
                value = password,
                onValueChange = {
                    password = it
                    passInputColor = Color.Gray
                    passInputInfo = ""
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Password, contentDescription = "password")
                },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            imageVector = if (passwordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = passInputColor,
                    unfocusedIndicatorColor = passInputColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            AnimatedVisibility(visible = passInputInfo.isNotEmpty()) {
                Text(text = passInputInfo, color = Color.Red, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button
        Button(
            onClick = {
                var isValid = true

                if (signUpState && name.trim().isEmpty()) {
                    nameInputColor = Color.Red
                    nameInputInfo = "Name can't be empty"
                    isValid = false
                }

                if (email.trim().isEmpty()) {
                    emailInputColor = Color.Red
                    emailInputInfo = "Email can't be empty"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInputColor = Color.Red
                    emailInputInfo = "Invalid email format"
                    isValid = false
                }

                if (password.length < 6) {
                    passInputColor = Color.Red
                    passInputInfo = "Password must be at least 6 characters"
                    isValid = false
                }

                if (isValid) {
                    focusManager.clearFocus(true)
                    if(!networkStatus){
                        Toast.makeText(context, "Connect to internet first", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        enabled = false
                        if (signUpState) {
                            viewModel.signUp(name, email, password, {
                                Toast.makeText(
                                    context,
                                    "Account Created Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                                navigateToHome()
                            }) {
                                Toast.makeText(context, "Error:$it", Toast.LENGTH_SHORT).show()
                                enabled = true
                            }
                        } else {
                            viewModel.signIn(email, password, {
                                Toast.makeText(
                                    context,
                                    "Logged In Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                               navigateToHome()
                            }) {
                                Toast.makeText(context, "Error:$it", Toast.LENGTH_SHORT).show()
                                enabled = true
                            }
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(blue20, blue40, blue80)
                    ),
                    shape = CircleShape
                )
        ) {
            Text(text = if (signUpState) "Sign up" else "Sign in")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = if (signUpState) "Already have an account?" else "Haven't created account yet?")
            Text(
                modifier = Modifier.clickable(enabled = enabled) {

                    signUpState = !signUpState
                    nameInputColor = Color.Gray
                    emailInputColor = Color.Gray
                    passInputColor = Color.Gray
                    nameInputInfo = ""
                    emailInputInfo = ""
                    passInputInfo = ""
                },
                text = if (signUpState) "Log In" else "Sign Up",
                color = blue40
            )
        }
    }
}