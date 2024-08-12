package xapics.app.presentation.screens.profile

import xapics.app.Thumb


data class ProfileState(
    val userCollections: List<Thumb>? = null,
    val connectionError: Boolean = false,
    val isLoading: Boolean = false
)