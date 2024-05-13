package xapics.app.ui

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xapics.app.OnPicsListScreenRefresh.GET_COLLECTION
import xapics.app.OnPicsListScreenRefresh.SEARCH
import xapics.app.Pic
import xapics.app.StateSnapshot
import xapics.app.TAG
import xapics.app.Tag
import xapics.app.TagState.DISABLED
import xapics.app.TagState.ENABLED
import xapics.app.TagState.SELECTED
import xapics.app.Thumb
import xapics.app.data.PicsApi
import xapics.app.data.auth.AuthRepository
import xapics.app.data.auth.AuthResult
import xapics.app.getTagColorAndName
import xapics.app.toTagsList
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor (
    private val api: PicsApi,
    private val repository: AuthRepository,
): ViewModel() {

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    var stateHistory: MutableList<StateSnapshot> = mutableListOf()

    var onPicsListScreenRefresh = Pair(SEARCH, "")

    init {
        authenticate()
//        getUserInfo {} // TODO needed?
        getRollThumbs()
        getRandomPic()
        getAllTags()
    }


    /*** AUTH-RELATED */

    fun signUpOrIn(user: String, pass: String, signUp: Boolean) {
        updateLoadingState(true)
        viewModelScope.launch {
            try {
                val result = if(signUp) repository.signUp(
                    username = user,
                    password = pass,
                ) else repository.signIn(
                    username = user,
                    password = pass,
                )
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "signUpOrIn(): ", e)
                resultChannel.send(AuthResult.ConnectionError())
                updateLoadingState(false)
            }
        }
    }

    fun authenticate() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                var result = repository.authenticate(::updateUserName)
                if (result is AuthResult.Unauthorized) {
                    repository.refreshTokens()
                    result = repository.authenticate(::updateUserName)
                }
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "viewModel authenticate(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun getUserInfo(goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                var result = repository.getUserInfo(::updateUserCollections)
                if (result is AuthResult.Unauthorized) {
                    result = repository.refreshTokens()
                    if (result is AuthResult.Unauthorized) {
                        goToAuthScreen()
                    } else {
                        repository.getUserInfo(::updateUserCollections)
                    }
                }
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "viewModel getUserInfo(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updateUserName(userName: String?) {
        _appState.update { it.copy(
            userName = userName,
        ) }
    }

    fun logOut() {
//        updateUserName(null)
        repository.logOut()
        updateTopBarCaption("Log in")
    }


    /*** COLLECTIONS */

    fun editCollection(collection: String, picId: Int, goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true)
                var result = repository.editCollection(
                    collection = collection,
                    picId = picId
                )
                when (result) {
                    is AuthResult.Authorized -> {
                        Log.d(TAG, "Pic $picId added to $collection")
                        getPicCollections(picId) // TODO locally (check success first). Same everywhere ^v
                    }
                    is AuthResult.Unauthorized -> {
                        result = repository.refreshTokens()
                        if (result is AuthResult.Unauthorized) {
                            rememberToGetBackAfterLoggingIn(true)
                            goToAuthScreen()
                            resultChannel.send(result)
                        } else {
                            repository.editCollection(collection, picId)
                            getPicCollections(picId)
                        }
                    }
                    else -> {
                        Log.d(TAG, "Unknown error")
                    }
                }
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "editCollection(): ", e)
//                updateLoadingState(false)
            }
        }
    }

    fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?, goToAuthScreen: () -> Unit) {
        // if renamedTitle == null -> delete collection
        viewModelScope.launch {
            try {
                fun run() {
                    val userCollections = appState.value.userCollections?.toMutableList()
                    val index = userCollections?.indexOfFirst { it.title == collectionTitle }
                    if (renamedTitle != null) {
                        Log.d(TAG, "Collection $collectionTitle renamed to $renamedTitle")
                        if (index != null && index != -1) {
                            userCollections[index] = Thumb(renamedTitle, userCollections[index].thumbUrl)
                        }
                    } else {
                        Log.d(TAG, "Collection $collectionTitle deleted")
                        if (index != null) {
                            userCollections.removeAt(index)
                        }
                    }
                    _appState.update { it.copy(
                        userCollections = userCollections
                    ) }
                }

                var result = repository.renameOrDeleteCollection(
                    collectionTitle = collectionTitle,
                    renamedTitle = renamedTitle
                )
                when (result) {
                    is AuthResult.Authorized -> {
                        run()
                    }
                    is AuthResult.Conflicted -> TODO()
                    is AuthResult.Unauthorized -> {
                        result = repository.refreshTokens()
                        if (result is AuthResult.Unauthorized) {
                            goToAuthScreen()
                            resultChannel.send(result)
                        } else {
                            repository.renameOrDeleteCollection(collectionTitle, renamedTitle)
                            run()
                        }
                    }
                    is AuthResult.UnknownError -> Log.d(TAG, "Unknown error")
                    is AuthResult.ConnectionError -> Log.d(TAG, "Connection error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "renameOrDeleteCollection: ", e)
            }
        }
    }

    fun getCollection(collection: String, goToAuthScreen: () -> Unit) {
        clearPicsList()
        updateLoadingState(true)
        updateTopBarCaption(collection)
        viewModelScope.launch {
            try {
                var result = repository.getCollection(collection, ::updatePicsList)
                if (result is AuthResult.Unauthorized) {
                    result = repository.refreshTokens()
                    if (result is AuthResult.Unauthorized) {
                        goToAuthScreen()
                        resultChannel.send(result)
                    } else {
                        repository.getCollection(collection, ::updatePicsList)
                    }
                }
                updateLoadingState(false)
                saveStateSnapshot()
            } catch (e: Exception) {
                onPicsListScreenRefresh = Pair(GET_COLLECTION, collection)
                showConnectionError(true)
                updateLoadingState(false)
                Log.e(TAG, "getCollection: ", e)
            }
        }
    }

    fun updateUserCollections(userCollections: List<Thumb>?) {
        _appState.update { it.copy(
            userCollections = userCollections,
        ) }
    }

    fun updateCollectionToSaveTo(collection: String) {
        val isNewCollection = appState.value.userCollections?.firstOrNull { it.title == collection } == null
        _appState.update { it.copy(
            collectionToSaveTo = collection,
        ) }
        if(isNewCollection) {
            _appState.update { it.copy(
                userCollections = appState.value.userCollections?.plus(Thumb(collection, appState.value.pic!!.imageUrl)),
            )}
        }
    }

    private fun getPicCollections(picId: Int) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true) // if uncomment -> tags in PicScreen will fade out before fade in
                val result = repository.getPicCollections(picId, ::updatePicCollections)
                if (result is AuthResult.Unauthorized) {
                    repository.refreshTokens()
                    repository.getPicCollections(picId, ::updatePicCollections)
                }
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.d(TAG, "getPicCollections: ", e)
                showConnectionError(true)
//                updateLoadingState(false)
            }
        }
    }

    private fun updatePicCollections(picCollections: List<String>) {
        _appState.update { it.copy(
            picCollections = picCollections,
        )}
    }

    /*** MAIN WORKFLOW */

    fun updatePicsList(picsList: List<Pic>? = null) {
        _appState.update { it.copy(
            picsList = picsList ?: emptyList(),
            picIndex = 0,
        )}
    }

    fun getRollThumbs() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    rollThumbnails = api.getRollThumbnails().sortedByDescending { roll -> roll.thumbUrl },
                    isLoading = false
                )}
            } catch (e: Exception) {
                showConnectionError(true)
                Log.e(TAG, "getRollThumbs(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun search(query: String) {
        clearPicsList()
        updateLoadingState(true)
        updateTopBarCaption(query)
        viewModelScope.launch {
            try {
                _appState.update { it.copy(
                    picsList = api.search(query),
                    picIndex = 0,
                    isLoading = false
                )}
                saveStateSnapshot()
            } catch (e: Exception) { // TODO if error 500 -> custom error message
                Log.e(TAG, "search: ", e)
                onPicsListScreenRefresh = Pair(SEARCH, query)
                showConnectionError(true)
                updateLoadingState(false)
            }
        }
    }

    fun updatePicState(picIndex: Int) {
        if (appState.value.picsList != null && appState.value.picsList!!.size > picIndex) {
            _appState.update {
                it.copy(
                    pic = appState.value.picsList!![picIndex],
                    picIndex = picIndex
                )
            }
            getPicCollections(appState.value.picsList!![picIndex].id)
        } else {
            _appState.update {
                it.copy(
                    pic = null,
                    picIndex = null
                )
            }
        }
    }

    fun getRandomPic() {
        viewModelScope.launch {
            try {
//                updateLoadingState(true)
                var randomPic = api.getRandomPic()
                if(randomPic == appState.value.pic) randomPic = api.getRandomPic()
                _appState.update { it.copy(
                    pic = randomPic,
//                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e(TAG, "getRandomPic: ", e)
//                updateLoadingState(false)
            }
        }
    }

    fun getAllTags() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)

                val tags = api.getAllTags().string.toTagsList()

                _appState.update { it.copy(
                    tags = tags,
                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e(TAG, "getAllTags: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun getFilteredTags(clickedTag: Tag) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)

                var selectedTags = appState.value.tags.filter { it.state == SELECTED }

                selectedTags = if (clickedTag.state == SELECTED) {
                    selectedTags.minus(clickedTag)
                } else {
                    selectedTags.plus(clickedTag)
                }

                val query = selectedTags.map {
                    "${it.type} = ${it.value}"
                }.toString().drop(1).dropLast(1)

                val filteredTags = (if (query.isEmpty()) {
                    api.getAllTags().string
                } else {
                    api.getFilteredTags(query).string
                }
                        ).toTagsList()

                val refreshedTags = appState.value.tags.toMutableList()
                refreshedTags.forEach { tag ->
                    val isClickedTag = clickedTag.type == tag.type && clickedTag.value == tag.value
                    val shouldBeEnabled = filteredTags.any { it.type == tag.type && it.value == tag.value }

                    when {
                        isClickedTag -> if (clickedTag.state == SELECTED) tag.state = ENABLED else tag.state = SELECTED
                        shouldBeEnabled -> if (tag.state != SELECTED) tag.state = ENABLED
                        else -> if (selectedTags.none { it.type == tag.type }) tag.state = DISABLED
                    }
                }

                _appState.update { it.copy(
                    tags = refreshedTags,
                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e(TAG, "getFilteredTags: ", e)
                updateLoadingState(false)
            }
        }
    }

    /** BACKSTACK / NAVIGATION */

    fun saveStateSnapshot() {
        stateHistory.add(
            StateSnapshot(
                appState.value.picsList,
                appState.value.pic,
                appState.value.picIndex,
                appState.value.topBarCaption,
            )
        )
    }

    fun loadStateSnapshot(): String {
        if (stateHistory.isNotEmpty()) Log.d(TAG, "loadStateSnapshot 111: ${stateHistory.last().topBarCaption}, ${appState.value.topBarCaption}")
        stateHistory.removeLast()
        if (stateHistory.isNotEmpty()) {
            val last = stateHistory.last()
            _appState.update { it.copy(
                picsList = last.picsList,
                pic = last.pic,
                picIndex = last.picIndex,
                topBarCaption = last.topBarCaption
            ) }
            Log.d(TAG, "loadStateSnapshot 222: ${stateHistory.last().topBarCaption}, ${appState.value.topBarCaption}")
        }
        return appState.value.topBarCaption
    }

    fun updateStateSnapshot() {
        stateHistory.last().pic = appState.value.pic
        stateHistory.last().picIndex = appState.value.picIndex
    }

    fun rememberToGetBackAfterLoggingIn(value: Boolean? = null) {
        _appState.update { it.copy(
            getBackAfterLoggingIn = value ?: appState.value.getBackAfterLoggingIn
        ) }
    }


    /*** VISUALS (state update) */

    private fun updateLoadingState(loading: Boolean) {
        _appState.update { it.copy(isLoading = loading) }
    }

    fun showConnectionError(show: Boolean){
        _appState.update { it.copy(
            showConnectionError = show
        )}
    }

    fun showSearch(show: Boolean) {
        _appState.update { it.copy(showSearch = show) }
    }

    fun showPicsList(show: Boolean) {
        _appState.update { it.copy(
            showPicsList = show
        ) }
    }

    fun updateTopBarCaption(query: String) {
        val caption: String

        if (!query.contains(" = ")) {
            caption = query
        } else {
            val tags = query.toTagsList()
            Log.d(TAG, "updateTopBarCaption: $tags")
            val searchIndex = tags.indexOfFirst{it.type == "search"}
            val isSearchQuery = searchIndex != -1
            val isFilteredList = tags.size > 1

            caption = when {
                tags.isEmpty() -> "??? $query"
                isSearchQuery -> "\"${tags[searchIndex].value}\""
                isFilteredList -> "Filtered pics"
                else -> { // single category
                    val theTag = tags[0].value
                    when (tags[0].type) {
                        "filmType" -> when (theTag) {
                            "BW" -> "Black and white films"
                            "NEGATIVE" -> "Negative films"
                            "SLIDE" -> "Slide films"
                            else -> ""
                        }
                        "filmName" -> "film: $theTag"
                        "hashtag" -> "#$theTag"
                        else -> getTagColorAndName(tags[0]).second
                    }
                }
            }
        }

        _appState.update { it.copy(
            topBarCaption = caption
        )}
    }

    private fun clearPicsList() {
        _appState.update { it.copy(
            picsList = null,
            picIndex = null,
        )}
    }

    fun changeFullScreenMode(fullscreen: Boolean? = null) {
        _appState.update { it.copy(
            isFullscreen = fullscreen ?: !appState.value.isFullscreen
        )}
    }

    fun updatePicDetailsWidth(width: Dp) {
        _appState.update { it.copy(
            picDetailsWidth = width
        ) }
    }

    fun changeBlurContent(blur: Boolean) {
        _appState.update { it.copy(blurContent = blur) }
    }
}