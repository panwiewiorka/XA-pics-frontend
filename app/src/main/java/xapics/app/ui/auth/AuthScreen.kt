package xapics.app.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
import xapics.app.auth.AuthResult

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    goToProfileScreen: () -> Unit,
) {
    val authState = viewModel.authState
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel, context) {
//        viewModel.getUserInfo()
//        viewModel.authenticate()

        viewModel.authResults.collect{ result ->
            when(result) {
                is AuthResult.Authorized -> {
                    Toast.makeText(
                        context,
                        "You're authorized",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.getAllCollections()
                    Log.d(TAG, "3 AuthScreen: ${viewModel.authState.userId}")
                    goToProfileScreen()
                }
                is AuthResult.Unauthorized -> {
                    Toast.makeText(
                        context,
                        "You're not authorized",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthResult.UnknownError -> {
                    Toast.makeText(
                        context,
                        "An unknown error occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(Unit) {
                viewModel.updateTopBarCaption("Log in")
            }
            var signupMode by rememberSaveable { mutableStateOf(false) }
            var userField by rememberSaveable { mutableStateOf("") }
            var passField by rememberSaveable { mutableStateOf("") }
            var passVisible by rememberSaveable { mutableStateOf(false) }

            TextField(
                value = userField,
                onValueChange = {
                    userField = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Username") },
                maxLines = 1,
            )
            TextField(
                value = passField,
                onValueChange = {
                    passField = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Password") },
                trailingIcon = {
                    val icon = if (passVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
                    val description = if (passVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(painterResource(icon), description)
                    }
                               },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                maxLines = 1,
            )
            if (signupMode) {
                Button(onClick = { viewModel.signUpOrIn(userField, passField, signupMode) }) {
                    Text("Sign up", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text(text = "Already have an account?  ")
                    Text(text = "Log in", textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                        viewModel.updateTopBarCaption("Log in")
                        signupMode = !signupMode
                    })
                }
            } else {
                Button(onClick = { viewModel.signUpOrIn(userField, passField, signupMode) }) {
                    Text(" Log in ", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text(text = "Don't have an account?  ")
                    Text(text = "Sign up", textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                        viewModel.updateTopBarCaption("Sign up")
                        signupMode = !signupMode
                    })
                }
            }

//        TextField(
//            value = authState.signUpUsername,
//            onValueChange = {
//                viewModel.onAuthEvent(AuthUiEvent.SignUpUsernameChanged(it))
//            },
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = {
//                Text(text = "Username")
//            }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        TextField(
//            value = authState.signUpPassword,
//            onValueChange = {
//                viewModel.onAuthEvent(AuthUiEvent.SignUpPasswordChanged(it))
//            },
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = {
//                Text(text = "Password")
//            }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = {
//                viewModel.onAuthEvent(AuthUiEvent.SignUp)
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text(text = "Sign up")
//        }
//
//        Spacer(modifier = Modifier.height(64.dp))
//
//        TextField(
//            value = authState.signInUsername,
//            onValueChange = {
//                viewModel.onAuthEvent(AuthUiEvent.SignInUsernameChanged(it))
//            },
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = {
//                Text(text = "Username")
//            }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        TextField(
//            value = authState.signInPassword,
//            onValueChange = {
//                viewModel.onAuthEvent(AuthUiEvent.SignInPasswordChanged(it))
//            },
//            modifier = Modifier.fillMaxWidth(),
//            placeholder = {
//                Text(text = "Password")
//            }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = {
//                viewModel.onAuthEvent(AuthUiEvent.SignIn)
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text(text = "Sign in")
//        }
        }
        if (authState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}