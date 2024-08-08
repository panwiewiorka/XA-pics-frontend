package xapics.app.presentation.screens.picsList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import xapics.app.Pic
import xapics.app.TAG
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

    var picsList = mutableStateListOf<Pic>()
        private set

    init {
        viewModelScope.launch {
            useCases.getSnapshotFlow().collectLatest { value ->
                value?.let { _state.value = value }
            }

//            picsList = useCases.getSnapshot().picsList.toMutableStateList()
        }
    }


    fun search(query: String) {
        isLoading = true
//        clearPicsList()
        viewModelScope.launch {
            try {
                picsList = useCases.searchPics(query).toMutableStateList()
                isLoading = false
            } catch (e: Exception) { // TODO if error 500 -> custom error message
//                onPicsListScreenRefresh = Pair(OnPicsListScreenRefresh.SEARCH, query) // todo
//                showConnectionError(true)
                isLoading = false
                Log.e(TAG, "search: ", e)
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