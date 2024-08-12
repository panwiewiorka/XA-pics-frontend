package xapics.app.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xapics.app.TAG
import xapics.app.domain.useCases.UseCases
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val useCases: UseCases,
): ViewModel() {

    var searchIsShown by mutableStateOf(false)
        private set

    var isFullscreen by mutableStateOf(false)
        private set

    fun showSearch(show: Boolean) {
        searchIsShown = show
    }

    fun changeFullscreenMode(fullscreenOn: Boolean? = null) {
        isFullscreen = fullscreenOn ?: !isFullscreen
    }

    fun loadCaption() {
        viewModelScope.launch {
            try {
                useCases.loadCaption()
            } catch (e: Exception) {
                Log.e(TAG, "loadCaption: ", e)
            }
        }
    }
}