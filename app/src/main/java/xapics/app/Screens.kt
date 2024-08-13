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
    data object Search: Screen()

    @Serializable
    data class Auth(val goBackAfterLogIn: Boolean): Screen() {
        companion object {
            const val NAME = "Auth"
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<Auth>()
        }
    }

    @Serializable
    data class Profile(val userName: String): Screen() {
        companion object {
            const val NAME = "Profile"
            fun from(savedStateHandle: SavedStateHandle) = savedStateHandle.toRoute<Profile>()
        }
    }

}