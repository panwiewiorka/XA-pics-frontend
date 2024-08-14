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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import xapics.app.Pic
import xapics.app.TAG
import xapics.app.data.auth.AuthResult
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import xapics.app.presentation.screens.Screen
import javax.inject.Inject


@HiltViewModel
class PicsListViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val query = Screen.PicsList.from(savedStateHandle).searchQuery
    private val collectionPrefix = "collection = "

    var isLoading by mutableStateOf(false)
        private set

    var connectionError by mutableStateOf(false)
        private set

    var picsList = mutableStateListOf<Pic>()
        private set

    private val resultChannel = Channel<AuthResult<String?>>()
    val authResults = resultChannel.receiveAsFlow()

    private val messagesChannel = Channel<String>()
    val messages = messagesChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            if (query.contains(collectionPrefix)) {
                getCollection(query.substringAfter(collectionPrefix))
            } else search()
        }
    }


    fun search() {
        isLoading = true
        viewModelScope.launch {
            try {
                picsList = useCases.searchPics(query).toMutableStateList()
            } catch (e: HttpException) {
                if (e.code() == 500) messagesChannel.send(e.message()) else showConnectionError(true)
                Log.e(TAG, "search: ", e)
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "search: ", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun getCollection(collection: String) {
        isLoading = true

        viewModelScope.launch {
            try {
                val result = authRepository.getCollection(collection)
                when(result) {
                    is AuthResult.Authorized -> picsList = useCases.getStateSnapshot().picsList.toMutableStateList()
                    is AuthResult.Unauthorized -> resultChannel.send(result)
                    else -> { }
                }
                isLoading = false
            } catch (e: HttpException) {
                if (e.code() == 500) messagesChannel.send(e.message()) else showConnectionError(true)
                Log.e(TAG, "search: ", e)
            } catch (e: Exception) {
                showConnectionError(true)
                isLoading = false
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    fun saveCaption() {
        viewModelScope.launch {
            try {
                useCases.saveCaption(false, null)
            } catch (e: Exception) {
                Log.e(TAG, "saveCaption (PicsListVM): ", e)
            }
        }
    }

    fun showConnectionError(show: Boolean) {
        connectionError = show
    }

}