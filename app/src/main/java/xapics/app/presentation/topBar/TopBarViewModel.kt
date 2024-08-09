package xapics.app.presentation.topBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xapics.app.data.db.StateSnapshot
import xapics.app.domain.auth.AuthRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class TopBarViewModel @Inject constructor (
    private val authRepository: AuthRepository,
    private val useCases: UseCases,
): ViewModel() {

//    val topBarCaptionFlow = useCases.getTopBarCaptionFlow()

    private val _state = MutableStateFlow(StateSnapshot())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.getSnapshotFlow().collect { value ->
                value?.let { _state.value = value }
            }
        }
    }


    fun logOut() {
        authRepository.logOut()
        viewModelScope.launch{
            useCases.saveSnapshot(replaceExisting = true, topBarCaption = "Log in")
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