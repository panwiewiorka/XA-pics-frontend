package xapics.app.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.domain.PicsRepository
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val picsRepository: PicsRepository,
    private val useCases: UseCases,
): ViewModel() {

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    init {
        authenticate()
        getRandomPic()
        getRollThumbs()
        getAllTags()
    }


    fun authenticate() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                authRepository.authenticate(::updateUserName)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "viewModel authenticate(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updateUserName(userName: String?) {
        _appState.update { it.copy(
            userName = userName,
        ) }
    }

    fun getCollection(collection: String, goToAuthScreen: (isAuthorized: Boolean) -> Unit) {
        updateLoadingState(true)
        viewModelScope.launch {
            try {
                val result = authRepository.getCollection(collection)
                if (result is AuthResult.Unauthorized) {
                    goToAuthScreen(false)
//                    resultChannel.send(result)
                }
                updateLoadingState(false)
            } catch (e: Exception) {
                showConnectionError(true)
                updateLoadingState(false)
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    /*** MAIN WORKFLOW */

    fun getRollThumbs() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    rollThumbnails = picsRepository.getRollThumbs(),
                    isLoading = false
                )}
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "getRollThumbs(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun getRandomPic() {
        viewModelScope.launch {
            try {
                var pic = picsRepository.getRandomPic()
                if (pic == appState.value.randomPic) pic = picsRepository.getRandomPic()
                _appState.update { it.copy(
                    randomPic = pic
                ) }
                useCases.updateStateSnapshot(picsList = listOf(pic))
            } catch (e: Exception) {
                Log.e(TAG, "getRandomPic: ", e)
            }
        }
    }

    fun getAllTags() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)

                val tags = picsRepository.getAllTags()
                useCases.updateStateSnapshot(tags = tags)

                _appState.update { it.copy(
                    tags = tags,
                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e(TAG, "getAllTags: ", e)
                updateLoadingState(false)
            }
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


    /*** VISUALS (state update) */

    private fun updateLoadingState(loading: Boolean) {
        _appState.update { it.copy(isLoading = loading) }
    }

    fun showConnectionError(show: Boolean){
        _appState.update { it.copy(
            connectionError = show
        )}
    }

    fun showSearch(show: Boolean) {
        _appState.update { it.copy(showSearch = show) }
    }

    fun changeFullScreenMode(fullscreen: Boolean? = null) {
        _appState.update { it.copy(
            isFullscreen = fullscreen ?: !appState.value.isFullscreen
        )}
    }
}