package xapics.app

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Home: Screen()

    @Serializable
    data class PicsList(val searchQuery: String): Screen() {
        companion object {
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<PicsList>()
        }
    }

    @Serializable
    data class Pic(val picIndex: Int): Screen() {
        companion object {
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<Pic>()
        }
    }

    @Serializable
    data object Search: Screen()

    @Serializable
    data object Auth: Screen()

    @Serializable
    data object Profile: Screen()
}