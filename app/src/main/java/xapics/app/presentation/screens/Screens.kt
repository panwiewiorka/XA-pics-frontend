package xapics.app.presentation.screens

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Home: Screen() {
        const val NAME = "Home"
    }

    @Serializable
    data class PicsList(val searchQuery: String): Screen() {
        companion object {
            const val NAME = "PicsList"
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<PicsList>()
        }
    }

    @Serializable
    data class Pic(val picIndex: Int): Screen() {
        companion object {
            const val NAME = "Pic"
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<Pic>()
        }
    }

    @Serializable
    data object Search: Screen() {
        const val NAME = "Search"
    }

    @Serializable
    data class Auth(val goBackAfterLogIn: Boolean): Screen() {
        companion object {
            const val NAME = "Auth"
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<Auth>()
        }
    }

    @Serializable
    data object Profile: Screen() {
        const val NAME = "Profile"
    }

}