package xapics.app.presentation.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.Tag
import xapics.app.domain.PicsRepository
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor (
    private val picsRepository: PicsRepository,
    private val useCases: UseCases,
): ViewModel() {

    var tags by mutableStateOf(emptyList<Tag>())
        private set

    var isLoading by mutableStateOf(false)
        private set


    init {
        viewModelScope.launch {
            try {
                useCases.saveCaption(false, "Search")
            } catch (e: Exception) {
                Log.e(TAG, "saveCaption (SearchScreen): ", e)
            }
        }
        getAllTags()
    }


    fun search(query: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                useCases.searchPics(query)
                isLoading = false

            } catch (e: Exception) {
                Log.e(TAG, "search: ", e)
//                onPicsListScreenRefresh = Pair(OnPicsListScreenRefresh.SEARCH, query) // todo
//                showConnectionError(true) // todo
                isLoading = false

            }
        }
    }

    fun getAllTags() {
        viewModelScope.launch {
            try {
                isLoading = true
                tags = picsRepository.getAllTags()
                isLoading = false
            } catch (e: Exception) {
                Log.e(TAG, "getAllTags: ", e)
                isLoading = false
            }
        }
    }

    fun getFilteredTags(clickedTag: Tag) {
        viewModelScope.launch {
            try {
                isLoading = true
                // todo replace Tag with immutable version and use mutableStateListOf()
                val tempList = picsRepository.getFilteredTags(clickedTag, tags)
                tags = listOf()
                tags = tempList
                isLoading = false

            } catch (e: Exception) {
                Log.e(TAG, "getFilteredTags: ", e)
                isLoading = false
            }
        }
    }
}