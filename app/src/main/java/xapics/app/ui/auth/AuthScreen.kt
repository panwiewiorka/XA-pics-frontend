package xapics.app.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xapics.app.R
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.ui.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    popBackStack: () -> Unit,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
    isLoading: Boolean,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.updateTopBarCaption("Log in")
//        viewModel.authenticate()
        Log.d(TAG, "AuthScreen is shown")
    }

    LaunchedEffect(viewModel, context) {
//        viewModel.authenticate()
        Log.d(TAG, "AuthScreen: Launched Effect started")

        viewModel.authResults.collect { result ->
            Log.d(TAG, "AuthScreen: result is $result")
            val resultUserName = result.data.toString()
            Log.d(TAG, "AuthScreen: resultUserName is $resultUserName")
            if (resultUserName != "null") {
                when(result) {
                    is AuthResult.Authorized -> {
                        val stateUserName = viewModel.appState.value.userName
                        if(resultUserName != stateUserName) viewModel.updateUserName(resultUserName)
                        Log.d(TAG, "updateUserName(): result Name = $resultUserName, state Name = $stateUserName")
                        when {
                            viewModel.appState.value.getBackAfterLoggingIn -> {
                                viewModel.rememberToGetBackAfterLoggingIn(false)
                                popBackStack()
                            }
                            viewModel.appState.value.userName == null -> {
                                Toast.makeText(
                                    context,
                                    "username = null",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            viewModel.appState.value.userName == "admin" -> {
                                popBackStack()
                                goToAdminScreen()
                            }
                            else -> {
                                popBackStack()
                                goToProfileScreen()
                            }
                        }
                    }
                    is AuthResult.Conflicted -> {
                        Toast.makeText(
                            context,
                            result.data.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is AuthResult.Unauthorized -> {
                        Toast.makeText(
                            context,
                            "You are not authorized",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is AuthResult.UnknownError -> {
                        Toast.makeText(
                            context,
                            "An unknown error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is AuthResult.ConnectionError -> {
                        Toast.makeText(
                            context,
                            "No connection to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else if (result is AuthResult.ConnectionError) {
                Toast.makeText(
                    context,
                    "No connection to server",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
                viewModel.showSearch(false)
            },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var signupMode by rememberSaveable { mutableStateOf(false) }
            var userField by rememberSaveable { mutableStateOf("") }
            var passField by rememberSaveable { mutableStateOf("") }
            var passVisible by rememberSaveable { mutableStateOf(false) }

            OutlinedTextField(
                value = userField,
                onValueChange = {
                    userField = it
                },
                placeholder = { Text(text = "Username") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequester.requestFocus() }
                ),
                modifier = Modifier
                    .width(280.dp)
            )

            OutlinedTextField(
                value = passField,
                onValueChange = {
                    passField = it
                },
                placeholder = { Text(text = "Password") },
                trailingIcon = {
                    val icon = if (passVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24
                    val description = if (passVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(painterResource(icon), description)
                    }
                               },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.signUpOrIn(userField, passField, signupMode)
                        focusManager.clearFocus()
                    }
                ),
                maxLines = 1,
                modifier = Modifier
                    .width(280.dp)
                    .focusRequester(focusRequester)
            )

            val (buttonText, questionText, changeModeText) = if (signupMode) {
                Triple("Sign up", "Already have an account?  ", "Log in")
            } else {
                Triple(" Log in ", "Don't have an account?   ", "Sign up")
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(68.dp)
            ) {
                Button(
                    enabled = !isLoading,
                    onClick = {
                        viewModel.signUpOrIn(userField, passField, signupMode)
                        focusManager.clearFocus()
                    }
                ) {
                    Text(buttonText, fontSize = 18.sp)
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = questionText)

                Text(text = changeModeText, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                    viewModel.updateTopBarCaption(changeModeText)
                    signupMode = !signupMode
                })
            }
        }
    }
}