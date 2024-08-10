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

    init {
        viewModelScope.launch {
            try {
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

    fun onGoToSearchScreen() {
        _captionState.update { "Search" }
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

//    private fun updateTopBarCaptionState(caption: String) {
//        _state.update { it.copy(
//            topBarCaption = updateTopBarCaption(caption)
//        ) }
//    }
//
//    fun loadStateSnapshot(): String {
//        if (stateHistory.isNotEmpty()) Log.d(TAG, "loadStateSnapshot 111: ${stateHistory.last().topBarCaption}, ${state.value.topBarCaption}")
//        stateHistory.removeLast()
//        if (stateHistory.isNotEmpty()) {
//            val last = stateHistory.last()
//            _state.update { it.copy(
//                picsList = last.picsList,
//                pic = last.pic,
//                picIndex = last.picIndex,
//                topBarCaption = last.topBarCaption
//            ) }
//            Log.d(TAG, "loadStateSnapshot 222: ${stateHistory.last().topBarCaption}, ${state.value.topBarCaption}")
//        }
//        return state.value.topBarCaption
//    }
//
//    fun updateStateSnapshot() {
//        stateHistory.last().pic = state.value.pic
//        stateHistory.last().picIndex = state.value.picIndex
//    }
//
//
//    fun showSearch(show: Boolean) {
//        _state.update { it.copy(showSearch = show) }
//    }
//
//    fun showPicsList(show: Boolean) {
//        _state.update { it.copy(
//            showPicsList = show
//        ) }
//    }
}