package xapics.app.presentation.topBar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.data.db.StateSnapshot
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class TopBarViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
): ViewModel() {

    private val _captionState = MutableStateFlow("XA pics")
    val captionState = _captionState.asStateFlow()

    private val _stateSnapshot = MutableStateFlow(StateSnapshot())
    val stateSnapshot = _stateSnapshot.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                launch {// separate coroutine because collecting makes subsequent code unreachable
                    useCases.populateStateSnapshot()
                    useCases.getStateSnapshotFlow().collect { snapshot ->
                        _stateSnapshot.update { it.copy(tags = snapshot.tags) }
                    }
                }
                useCases.populateCaptionTable()
                useCases.getCaptionFlow().collect { value ->
                    value?.let { _captionState.value = value.topBarCaption }
                }
            } catch (e: Exception) {
                Log.e(TAG, "topBarViewModel INIT: ", e)
            }
        }
    }


    fun logOut() {
        authRepository.logOut()
        viewModelScope.launch{
            useCases.saveCaption(replaceExisting = true, topBarCaption = "Log in")
        }
    }

    fun onProfileClick(goToAuthScreen: (isAuthorized: Boolean) -> Unit, goToProfileScreen: (userName: String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = authRepository.authenticate()
                if (result is AuthResult.Authorized) {
                    useCases.saveCaption(false, result.data!!)
                    goToProfileScreen(result.data)
                } else goToAuthScreen(false)
            } catch (e: Exception) {
                Log.e(TAG, "getUserName: ", e)
            }
        }
    }

    fun onGoToSearchScreen() {
        _captionState.update { "Search" } // fixing temporal visual glitch of switching to another caption
        saveCaption("Search")
    }

    fun saveCaption(caption: String) {
        viewModelScope.launch {
            try {
                useCases.saveCaption(false, caption)
            } catch (e: Exception) {
                Log.e(TAG, "saveCaption: ", e)
            }
        }
    }

    fun loadCaption() {
        viewModelScope.launch {
            try {
                useCases.loadCaption()
            } catch (e: Exception) {
                Log.e(TAG, "loadCaption: ", e)
            }
        }
    }

}