package xapics.app.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.Thumb
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

    private val resultChannel = Channel<AuthResult<String?>>()
    val authResults = resultChannel.receiveAsFlow()


    init {
        authenticate()
        getRandomPic()
        getRollThumbs()
        getAllTags()
    }


    /*** AUTH-RELATED */

    fun signUpOrIn(user: String, pass: String, signUp: Boolean) {
        updateLoadingState(true)
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
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "signUpOrIn(): ", e)
                resultChannel.send(AuthResult.ConnectionError())
                updateLoadingState(false)
            }
        }
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

    fun getUserInfo(goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                val result = authRepository.getUserCollections(::updateUserCollections)
                if (result is AuthResult.Unauthorized) {
                    goToAuthScreen()
                    resultChannel.send(result)
                }
                updateLoadingState(false)
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "viewModel getUserInfo(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updateUserName(userName: String?) {
        _appState.update { it.copy(
            userName = userName,
        ) }
    }


    /*** COLLECTIONS */

    fun editCollection(collection: String, picId: Int, goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true)
                val result = authRepository.editCollection(
                    collection = collection,
                    picId = picId
                )
                when (result) {
                    is AuthResult.Authorized -> {
                        getPicCollections(picId)
                    }
                    is AuthResult.Unauthorized -> {
                        rememberToGetBackAfterLoggingIn(true)
                        goToAuthScreen()
                        resultChannel.send(result)
                    }
                    else -> {
                        Log.d(TAG, "Unknown error") // TODO send message to channel to Toast
                    }
                }
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "editCollection(): ", e)
//                updateLoadingState(false)
            }
        }
    }

    fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?, goToAuthScreen: () -> Unit) {
        /** if (renamedTitle == null) -> delete collection */
        viewModelScope.launch {
            try {
                val result = authRepository.renameOrDeleteCollection(
                    collectionTitle = collectionTitle,
                    renamedTitle = renamedTitle
                )
                when (result) {
                    is AuthResult.Authorized -> {
                        val userCollections = appState.value.userCollections?.toMutableList()
                        val index = userCollections?.indexOfFirst { it.title == collectionTitle }
                        if (renamedTitle != null) {
                            Log.d(TAG, "Collection $collectionTitle renamed to $renamedTitle")
                            if (index != null && index != -1) {
                                userCollections[index] = Thumb(renamedTitle, userCollections[index].thumbUrl)
                            }
                        } else {
                            Log.d(TAG, "Collection $collectionTitle deleted")
                            if (index != null) {
                                userCollections.removeAt(index)
                            }
                        }
                        _appState.update { it.copy(
                            userCollections = userCollections
                        ) }
                    }
                    is AuthResult.Conflicted -> TODO()
                    is AuthResult.Unauthorized -> {
                        goToAuthScreen()
                        resultChannel.send(result)
                    }
                    is AuthResult.UnknownError -> Log.d(TAG, "Unknown error")
                    is AuthResult.ConnectionError -> Log.d(TAG, "Connection error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "renameOrDeleteCollection: ", e)
            }
        }
    }

    fun getCollection(collection: String, goToAuthScreen: () -> Unit) {
        updateLoadingState(true)
        viewModelScope.launch {
            try {
                val result = authRepository.getCollection(collection)
                if (result is AuthResult.Unauthorized) {
                    goToAuthScreen()
                    resultChannel.send(result)
                }
                updateLoadingState(false)
            } catch (e: Exception) {
                showConnectionError(true)
                updateLoadingState(false)
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    fun updateUserCollections(userCollections: List<Thumb>?) {
        _appState.update { it.copy(
            userCollections = userCollections,
        ) }
    }

    private fun getPicCollections(picId: Int) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true) // if uncomment -> tags in PicScreen will fade out before fade in
                authRepository.getPicCollections(picId, ::updatePicCollections)
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.d(TAG, "getPicCollections: ", e)
                showConnectionError(true)
//                updateLoadingState(false)
            }
        }
    }

    private fun updatePicCollections(picCollections: List<String>) {
        _appState.update { it.copy(
            picCollections = picCollections,
        )}
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

    /*** BACKSTACK / NAVIGATION */

    fun updateStateSnapshotWithListOfRandomPic() {
        viewModelScope.launch {
            useCases.updateStateSnapshot(listOf(appState.value.randomPic!!))
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

    fun loadCaption() {
        viewModelScope.launch {
            useCases.loadCaption()
        }
    }

    fun rememberToGetBackAfterLoggingIn(value: Boolean? = null) {
        _appState.update { it.copy(
            getBackAfterLoggingIn = value ?: appState.value.getBackAfterLoggingIn
        ) }
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