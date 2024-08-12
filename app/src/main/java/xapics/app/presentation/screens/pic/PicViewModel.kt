package xapics.app.presentation.screens.pic

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.Screen
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.data.auth.AuthResult
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class PicViewModel @Inject constructor (
//    private val picsRepository: PicsRepository,
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _picScreenState = MutableStateFlow(PicScreenState())
    val picScreenState: StateFlow<PicScreenState> = _picScreenState.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                _picScreenState.update { it.copy(
                    picsList = useCases.getStateSnapshot().picsList,
                    picIndex = Screen.Pic.from(savedStateHandle).picIndex
                ) }
            } catch (e: Exception) {
                showConnectionError(true)
                updateLoadingState(false)
                Log.e(TAG, "getSnapshot (get picsList): ", e)
            }
        }
    }


    fun saveCaption() { // todo remove?
        viewModelScope.launch {
//            useCases.saveCaption(true, null)
//            authRepository.getPicCollections(pic.id, ::updatePicCollections) // todo in PicScreen via LaunchedEffect?
        }
    }

    fun getCollection(collection: String, goToAuthScreen: () -> Unit) {
        updateLoadingState(true)
        viewModelScope.launch {
            try {
                val result = authRepository.getCollection(collection)
                if (result is AuthResult.Unauthorized) goToAuthScreen()
                updateLoadingState(false)

            } catch (e: Exception) {
//                showConnectionError(true)
                updateLoadingState(false)
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    fun editCollection(collection: String, picId: Int, goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true)
                val result = authRepository.editCollection(
                    collection = collection,
                    picId = picId
                )
                when (result) {
                    is AuthResult.Authorized -> getPicCollections(picId)
                    is AuthResult.Unauthorized -> goToAuthScreen()
                    else -> Log.d(TAG, "Unknown error") // TODO send message to channel to Toast
                }
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "editCollection(): ", e)
//                updateLoadingState(false)
            }
        }
    }

    private fun getPicCollections(picId: Int) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true) // if uncomment -> tags in PicScreen will fade out before fade in
                authRepository.getPicCollections(picId, ::updatePicCollections)
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.d(TAG, "getPicCollections: ", e)
//                showConnectionError(true)
//                updateLoadingState(false)
            }
        }
    }

    private fun updatePicCollections(picCollections: List<String>) {
        _picScreenState.update { it.copy(
            picCollections = picCollections,
        )}
    }

    fun updateCollectionToSaveTo(collection: String) {
        val isNewCollection = picScreenState.value.userCollections?.firstOrNull { it.title == collection } == null
        _picScreenState.update { it.copy(
            collectionToSaveTo = collection,
        ) }
        if(isNewCollection) {
            _picScreenState.update { it.copy(
                userCollections = picScreenState.value.userCollections?.plus(Thumb(collection, picScreenState.value.picsList[picScreenState.value.picIndex!!].imageUrl)),
            )}
        }
    }

    fun updatePicInfo(index: Int) {
        getPicCollections(picScreenState.value.picsList[index].id)
        _picScreenState.update { it.copy(
            picIndex = index,
        ) }
    }

    fun showConnectionError(show: Boolean){
        _picScreenState.update { it.copy(
            connectionError = show
        )}
    }

    fun changeFullScreenMode(fullscreen: Boolean? = null) {
        _picScreenState.update { it.copy(
            isFullscreen = fullscreen ?: !picScreenState.value.isFullscreen
        )}
    }

    private fun updateLoadingState(loading: Boolean) {
        _picScreenState.update { it.copy(isLoading = loading) }
    }

}