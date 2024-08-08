package xapics.app.presentation.screens.picsList

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class PicsListViewModel @Inject constructor (
    private val useCases: UseCases,
): ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private val _state = MutableStateFlow(StateSnapshot())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            useCases.getSnapshotFlow().collect { value ->
                value?.let { _state.value = value }
            }
        }
    }

    fun saveStateSnapshot(
        pic: Pic,
        picIndex: Int,
    ) {
        viewModelScope.launch {
            useCases.saveSnapshot(false, null, pic, picIndex, null)
//            authRepository.getPicCollections(pic.id, ::updatePicCollections) // todo in PicScreen via LaunchedEffect?
        }
    }

}