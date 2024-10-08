package xapics.app.presentation.screens.profile

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
import xapics.app.Thumb
import xapics.app.data.auth.AuthResult
import xapics.app.domain.auth.AuthRepository
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val authRepository: AuthRepository,
): ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()


    fun getUserInfo(goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                val result = authRepository.getUserCollections(::updateUserCollections) // todo replace with data in result
                if (result is AuthResult.Unauthorized) goToAuthScreen()
                updateLoadingState(false)
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "viewModel getUserInfo(): ", e)
                updateLoadingState(false)
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
                        val userCollections = profileState.value.userCollections?.toMutableList()
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
                        _profileState.update { it.copy(
                            userCollections = userCollections
                        ) }
                    }
                    is AuthResult.Conflicted -> Log.d(TAG, "Conflicted")
                    is AuthResult.Unauthorized -> goToAuthScreen()
                    is AuthResult.UnknownError -> Log.d(TAG, "Unknown error")
                    is AuthResult.ConnectionError -> Log.d(TAG, "Connection error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "renameOrDeleteCollection: ", e)
            }
        }
    }

    fun updateUserCollections(userCollections: List<Thumb>?) {
        _profileState.update { it.copy(
            userCollections = userCollections,
        ) }
    }



    fun showConnectionError(show: Boolean){
        _profileState.update { it.copy(
            connectionError = show
        )}
    }

    private fun updateLoadingState(loading: Boolean) {
        _profileState.update { it.copy(isLoading = loading) }
    }

}