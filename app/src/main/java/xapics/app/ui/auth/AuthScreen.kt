package xapics.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
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
import xapics.app.data.auth.AuthResult
import xapics.app.ui.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    goBack: () -> Unit,
    goToProfileScreen: () -> Unit,
    isLoading: Boolean,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.updateTopBarCaption("Log in")
    }

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            val response = result.data.toString()
            if (result is AuthResult.Authorized) {
                viewModel.updateUserName(response)
                if (viewModel.appState.value.getBackAfterLoggingIn) {
                    viewModel.rememberToGetBackAfterLoggingIn(false)
                    goBack()
                } else {
                    goBack()
                    goToProfileScreen()
                }
            } else {
                val toastMessage = when (result) {
                    is AuthResult.Conflicted -> response
                    is AuthResult.ConnectionError -> "No connection to server"
                    is AuthResult.Unauthorized -> "You are not authorized"
                    else -> "An unknown error occurred"
                }
                if (response != "null" || result is AuthResult.ConnectionError) Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
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

            Spacer(modifier = Modifier.weight(1f))

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
                        if (userField.isBlank() || passField.isBlank()) {
                            Toast.makeText(context, "Fill in the text fields", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.signUpOrIn(userField, passField, signupMode)
                            focusManager.clearFocus()
                        }
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

            Spacer(modifier = Modifier.weight(2f))
        }
    }
}