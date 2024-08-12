package xapics.app.presentation.screens.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xapics.app.Screen
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val goBackAfterLogIn = Screen.Auth.from(savedStateHandle).goBackAfterLogIn
    private val isAuthorized = Screen.Auth.from(savedStateHandle).isAuthorized

    private val resultChannel = Channel<AuthResult<String?>>()
    val authResults = resultChannel.receiveAsFlow()

    var isLoading by mutableStateOf(false)
        private set


    init {
        viewModelScope.launch {
            val caption = useCases.getCaption().topBarCaption
            if (caption != "Log in") saveCaption( false, "Log in")

            if (isAuthorized) resultChannel.send(AuthResult.Authorized()) else resultChannel.send(AuthResult.Unauthorized())
        }
    }


    fun saveCaption(
        replaceExisting: Boolean,
        topBarCaption: String? = null
    ) {
        viewModelScope.launch {
            try {
                useCases.saveCaption(replaceExisting, topBarCaption)
            } catch (e: Exception) {
                Log.e(TAG, "saveCaption (authScreen / Log in): ", e)
            }
        }
    }

    fun signUpOrIn(user: String, pass: String, signUp: Boolean) {
        isLoading = true
        viewModelScope.launch {
            try {
                val result = if(signUp) authRepository.signUp(
                    username = user,
                    password = pass,
                ) else authRepository.signIn(
                    username = user,
                    password = pass,
                )
                resultChannel.send(result)
                isLoading = false
            } catch (e: Exception) {
                resultChannel.send(AuthResult.ConnectionError())
                isLoading = false
                Log.e(TAG, "signUpOrIn(): ", e)
            }
        }
    }

}