package xapics.app.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.domain.PicsRepository
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val picsRepository: PicsRepository,
    private val useCases: UseCases,
): ViewModel() {

//    private val _appState = MutableStateFlow(AppState())
//    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val resultChannel = Channel<AuthResult<String?>>()
    val authResults = resultChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            try {
                useCases.saveCaption(false, "Log in")
            } catch (e: Exception) {
                Log.e(TAG, "saveCaption (authScreen / Log in): ", e)
            }
        }
    }


    fun saveCaption(
        replaceExisting: Boolean,
        topBarCaption: String? = null
    ) {
        viewModelScope.launch {
            useCases.saveCaption(replaceExisting, topBarCaption)
        }
    }

}