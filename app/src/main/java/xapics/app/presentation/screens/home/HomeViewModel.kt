package xapics.app.presentation.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.Pic
import xapics.app.TAG
import xapics.app.domain.PicsRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor (
    private val picsRepository: PicsRepository,
    private val useCases: UseCases,
): ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        getRandomPic()
        getRollThumbs()
        getAllTags()
    }


    fun getRollThumbs() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _homeState.update { it.copy(
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
                if (pic == homeState.value.randomPic) pic = picsRepository.getRandomPic()
                _homeState.update { it.copy(
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

                _homeState.update { it.copy(
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

    fun updatePicsListToRandomPic(pic: Pic, goToPicScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                useCases.updateStateSnapshot(
                    picsList = listOf(pic),
                )
                goToPicScreen()
            } catch (e: Exception) {
                Log.e(TAG, "updatePicsListToRandomPic: ", e)
            }
        }
    }

    private fun updateLoadingState(loading: Boolean) {
        _homeState.update { it.copy(isLoading = loading) }
    }

    fun showConnectionError(show: Boolean){
        _homeState.update { it.copy(
            connectionError = show
        )}
    }
}