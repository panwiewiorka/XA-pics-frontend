package xapics.app.presentation.screens.picsList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xapics.app.Pic
import xapics.app.Screen
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class PicsListViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val query = Screen.PicsList.from(savedStateHandle).searchQuery

    var isLoading by mutableStateOf(false)
        private set

    var connectionError by mutableStateOf(false)
        private set

    var picsList = mutableStateListOf<Pic>()
        private set

    init {
        viewModelScope.launch {
            search()
        }
    }


    fun search() { // todo merge with getCollection?
        isLoading = true
        viewModelScope.launch {
            try {
                picsList = useCases.searchPics(query).toMutableStateList()
                isLoading = false
            } catch (e: Exception) { // TODO if error 500 -> custom error message
                connectionError = true
                isLoading = false
                Log.e(TAG, "search: ", e)
            }
        }
    }

    fun getCollection(collection: String, goToAuthScreen: (isAuthorized: Boolean) -> Unit) {
        isLoading = true

        viewModelScope.launch {
            try {
                val result = authRepository.getCollection(collection)
                if (result is AuthResult.Unauthorized) {
                    goToAuthScreen(false)
//                    resultChannel.send(result)
                }
                isLoading = false
            } catch (e: Exception) {
                showConnectionError(true)
                isLoading = false
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    fun saveCaption() {
        viewModelScope.launch {
            useCases.saveCaption(false, null)
//            authRepository.getPicCollections(pic.id, ::updatePicCollections) // todo in PicScreen via LaunchedEffect?
        }
    }

    fun showConnectionError(show: Boolean) {
        connectionError = show
    }

}