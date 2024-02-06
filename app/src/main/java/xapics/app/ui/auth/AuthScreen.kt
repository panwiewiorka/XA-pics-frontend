package xapics.app.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xapics.app.MainViewModel
import xapics.app.R
import xapics.app.TAG
import xapics.app.auth.AuthResult

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    goToAdminScreen: () -> Unit,
    goToProfileScreen: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.updateTopBarCaption("Log in")
//        viewModel.authenticate()
//        Log.d(TAG, "Authenticate once")
    }

    LaunchedEffect(viewModel, context) {
//        viewModel.authenticate()
        Log.d(TAG, "Launched Effect started")

        viewModel.authResults.collect { result ->
            Log.d(TAG, "result is $result")
            when(result) {
                is AuthResult.Authorized -> {
                    val resultId = result.data.toString().toIntOrNull()
                    val stateId = viewModel.appState.value.userId
                    if(resultId != null && resultId != stateId) viewModel.updateUserId(resultId)
                    Log.d(TAG, "updateUserId(): result = $resultId, userId = $stateId")
                    when (stateId) {
                        null -> {
                            Toast.makeText(
                                context,
                                "userID = null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        1 -> {
//                            viewModel.updateTopBarCaption("Admin console")
                            goToAdminScreen()
                        }
                        else -> {
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
                viewModel.changeShowSearchState(false)
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
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Username") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequester.requestFocus() }
                )
            )
            OutlinedTextField(
                value = passField,
                onValueChange = {
                    passField = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
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
                    onGo = { viewModel.signUpOrIn(userField, passField, signupMode) }
                ),
                maxLines = 1,
            )

            val (buttonText, questionText, changeModeText) = if (signupMode) {
                Triple("Sign up", "Already have an account?  ", "Log in")
            } else {
                Triple(" Log in ", "Don't have an account?   ", "Sign up")
            }

            Button(onClick = { viewModel.signUpOrIn(userField, passField, signupMode) }) {
                Text(buttonText, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(text = questionText)
                Text(text = changeModeText, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                    viewModel.updateTopBarCaption(changeModeText)
                    signupMode = !signupMode
                })
            }
        }
    }
}