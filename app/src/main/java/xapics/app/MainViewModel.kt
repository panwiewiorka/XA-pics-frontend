package xapics.app

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import xapics.app.auth.AuthRepository
import xapics.app.auth.AuthResult
import xapics.app.data.PicsApi
import xapics.app.TagState.*
import xapics.app.ui.theme.CollectionTag
import xapics.app.ui.theme.DefaultTag
import xapics.app.ui.theme.FilmTag
import xapics.app.ui.theme.GrayMedium
import xapics.app.ui.theme.RollAttribute
import xapics.app.ui.theme.YearTag
import java.io.File
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

const val TAG = "mytag"

@HiltViewModel
class MainViewModel @Inject constructor (
    private val api: PicsApi,
    private val repository: AuthRepository
): ViewModel() {

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    init {
        authenticate()
        getUserInfo {} // TODO needed?
//        getPicsList(2020)
        getRollsList()
        getRandomPic()
        getAllTags()
    }


    private fun updateLoadingState(loading: Boolean) {
        _appState.update { it.copy(isLoading = loading) }
    }

    fun signUpOrIn(user: String, pass: String, signUp: Boolean) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                val result = if(signUp) repository.signUp(
                    username = user,
                    password = pass
                ) else repository.signIn(
                    username = user,
                    password = pass
                )
//                _appState.update { it.copy(
//                    userId = 0,
//                    userCollections = null,
//                ) }
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "signUpOrIn(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun authenticate() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                val result = repository.authenticate(::updateUserName)
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "viewModel authenticate(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun logOut() {
        repository.logOut()
        _appState.update { it.copy(
            userName = null
        ) }
    }

    fun getUserInfo(goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                val result = repository.getUserInfo(::updateUserName, ::updateUserCollections)
                if (result is AuthResult.Unauthorized) goToAuthScreen()
//                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "getUserInfo(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updateUserName(userName: String?) {
        _appState.update { it.copy(
            userName = userName,
        ) }
    }

    private fun updateUserCollections(userCollections: List<Thumb>?) {
        _appState.update { it.copy(
            userCollections = userCollections,
        ) }
    }

    fun editCollectionOrLogIn(collection: String, picId: Int, goToAuthScreen: () -> Unit) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true)
                val result = repository.editCollection(
                    collection = collection,
                    picId = picId
                )
                when (result) {
                    is AuthResult.Authorized -> {
                        Log.d(TAG, "Pic $picId added to $collection")
                        getPicCollections(picId) // TODO locally (check success first). Same everywhere ^v
                    }
                    is AuthResult.Unauthorized -> {
                        rememberToGetBackAfterLoggingIn(true)
                        goToAuthScreen()
                        Log.d(TAG, "401 unauthorized")
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

    fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?) {
        // if renamedTitle == null -> delete collection
        viewModelScope.launch {
            try {
                val result = repository.renameOrDeleteCollection(
                    collectionTitle = collectionTitle,
                    renamedTitle = renamedTitle
                )
                when (result) {
                    is AuthResult.Authorized -> {
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
                    is AuthResult.Conflicted -> TODO()
                    is AuthResult.Unauthorized -> Log.d(TAG, "401 unauthorized")
                    is AuthResult.UnknownError -> Log.d(TAG, "Unknown error")
                }
            } catch (e: Exception) {
                Log.e(TAG, "renameOrDeleteCollection: ", e)
            }

        }
    }

    fun getCollection(collection: String) {
        updateTopBarCaption(collection)
        clearPicsList()
        _appState.update { it.copy(
            onRefresh = { getCollection(collection) }
        ) }
        viewModelScope.launch {
            try {
                repository.getCollection(collection, ::updatePicsList, ::updateTopBarCaption) // TODO remove ::updateTopBarCaption
//            addToCaptionList(collection)
//            updateTopBarCaption(collection)
            } catch (e: Exception) {
                Log.e(TAG, "getCollection: ", e)
            }
        }
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

    private fun updatePicsList(picsList: List<Pic>? = null) { // TODO partly replace with vv clearPicsList()
        _appState.update { it.copy(
            picsList = picsList ?: emptyList(),
            picIndex = 1,
        )}
    }

    private fun clearPicsList() {
        _appState.update { it.copy(
            picsList = null,
            picIndex = null,
        )}
    }

    fun getTagColorAndName(tag: Tag): Pair<Color, String> {
        return when(tag.type) {
            "filmType" -> Pair(GrayMedium, tag.value.lowercase())
            "nonXa" -> Pair(RollAttribute, if(tag.value == "false") "XA" else "non-XA")
            "expired" -> Pair(RollAttribute, if(tag.value == "false") "not expired" else "expired")
            "xpro" -> Pair(RollAttribute, if(tag.value == "false") "no cross-process" else "cross-process")
            "iso" -> Pair(GrayMedium, "iso ${tag.value}")
            "filmName" -> Pair(FilmTag, tag.value)
            "year" -> Pair(YearTag, tag.value)
            "hashtag" -> Pair(DefaultTag, tag.value)
            "collection" -> Pair(CollectionTag, tag.value)
            else -> Pair(Color.Transparent, tag.value)
        }
    }

    fun updateTopBarCaption(query: String) {
        val caption: String
        if (!query.contains(" = ")) {
            caption = query
        } else {
            val tags = query
                .split(", ")
                .map { it.split(" = ") }
                .map { Tag(it[0], it[1]) }

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
                        "filmType" -> "${theTag.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) }} films"
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

    private fun getPicCollections(picId: Int) {
        viewModelScope.launch {
            try {
//                updateLoadingState(true) // if uncomment -> tags in PicScreen will fade out before fade in
                repository.getPicCollections(picId, ::updatePicCollections)
//                updateLoadingState(false)
            } catch (e: Exception) {
                Log.d(TAG, "getPicCollections: ", e)
                changeConnectionErrorVisibility(true)
//                updateLoadingState(false)
            }
        }
    }

    private fun updatePicCollections(picCollections: List<String>) {
        _appState.update { it.copy(
            picCollections = picCollections,
        )}
    }

    fun getFilmsList() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    filmsList = api.getFilmsList(),
                    isLoading = false
                ) }
//                Log.d(TAG, state.value.filmsList.toString())

            } catch (e: Exception) {
                Log.e(TAG, "getFilmsList: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun postFilm(isNewFilm: Boolean, film: Film, ) {
        viewModelScope.launch {
            try {
                api.postFilm(
                    isNewFilm,
                    film.filmName,
                    film.iso ?: 0,
                    film.type,
                )
            } catch (e: Exception) {
                Log.e(TAG, "postFilm: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun getRollsList() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    filmsList = api.getFilmsList(),
                    rollsList = api.getRollsList(),
                    rollThumbnails = api.getRollThumbnails(),
                    isLoading = false
                )}
            } catch (e: Exception) {
                changeConnectionErrorVisibility(true)
                Log.e(TAG, "getRollsList(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun changeConnectionErrorVisibility(show: Boolean? = null){
        _appState.update { it.copy(
            showConnectionError = show ?: !appState.value.showConnectionError
        )}
    }

    fun postRoll(isNewRoll: Boolean, roll: Roll, ) {
        viewModelScope.launch {
            try {
                api.postRoll(
                    isNewRoll,
                    roll.title,
                    roll.film,
                    roll.xpro,
                    roll.expired,
                    roll.nonXa
                )
            } catch (e: Exception) {
                Log.e(TAG, "postRoll(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun search(query: String) {
        updateLoadingState(true)
        clearPicsList()
        updateTopBarCaption(query)
        viewModelScope.launch {
            try {
                _appState.update { it.copy(
                    onRefresh = { search(query) },
                    picsList = api.search(query),
                    picIndex = 1,
                    isLoading = false
                )}
            } catch (e: Exception) {
                Log.e(TAG, "search: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun changeShowSearchState(showSearch: Boolean? = null) {
        _appState.update { it.copy(showSearch = showSearch ?: !appState.value.showSearch) }
    }

    fun rememberToGetBackAfterLoggingIn(value: Boolean? = null) {
        _appState.update { it.copy(
            getBackAfterLoggingIn = value ?: appState.value.getBackAfterLoggingIn
        ) }
    }

    fun updatePicState(picIndex: Int) {
        Log.d(TAG, "updatePicState picIndex: ${appState.value.picsList?.get(picIndex)!!.id}")
        _appState.update {
            it.copy(
                pic = appState.value.picsList?.get(picIndex), // TODO could picsList be null?
                picIndex = picIndex
            )
        }
        getPicCollections(appState.value.picsList?.get(picIndex)!!.id)
    }

    fun selectFilmToEdit(film: Film?) {
        _appState.update { it.copy( filmToEdit = film) }
    }

    fun editFilmField(
        filmName: String? = null,
        iso: Int? = null,
        type: FilmType? = null,
    ) {
        val film = Film(
            filmName ?: appState.value.filmToEdit!!.filmName,
            iso ?: appState.value.filmToEdit!!.iso,
            type ?: appState.value.filmToEdit!!.type,
        )
        _appState.update { it.copy(filmToEdit = film) }
    }

    fun updateFilmsListState(list: List<Film>) {
        _appState.update { it.copy( filmsList = list) }
    }

    fun selectRollToEdit(roll: Roll?) {
        _appState.update { it.copy( rollToEdit = roll) }
    }

    fun editRollField(
        title: String? = null,
        film: String? = null,
        xpro: Boolean? = null,
        expired: Boolean? = null,
        nonXa: Boolean? = null,
    ) {
        Log.d(TAG, "editRollField: film = $film")
        val roll = Roll(
            title ?: appState.value.rollToEdit!!.title,
            film ?: appState.value.rollToEdit!!.film,
            xpro ?: appState.value.rollToEdit!!.xpro,
            expired ?: appState.value.rollToEdit!!.expired,
            nonXa ?: appState.value.rollToEdit!!.nonXa,
        )
        _appState.update { it.copy(rollToEdit = roll) }
    }

    fun updateRollsListState(list: List<Roll>) {
        _appState.update { it.copy( rollsList = list) }
    }

    private suspend fun tryUploadImage(rollTitle: String, file: File): Boolean { // TODO merge with v uploadImage() ?
        return try {
            api.uploadImage(
                MultipartBody.Part
                    .createFormData(
                        "metadataPath",
                        rollTitle
                    ),
//                MultipartBody.Part
//                    .createFormData(
//                        "metadataName",
//                        ""
//                    ),
                MultipartBody.Part
                    .createFormData(
                        "image",
                        file.name,
                        file.asRequestBody()
                    )
            )
            file.delete()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: HttpException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            Log.e(TAG, "tryUploadImage: ", e)
            false
        }
    }

    fun uploadImage(rollTitle: String, file: File) {
        viewModelScope.launch {
            tryUploadImage(rollTitle, file)
        }
    }

    fun getRandomPic() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    pic = api.getRandomPic(),
                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e(TAG, "getRandomPic: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun getAllTags() {
        viewModelScope.launch {
            try {
                updateLoadingState(true)

                val tags = api.getAllTags().string
                    .split(", ")
                    .map { it.split(" = ") }
                    .map { Tag(it[0], it[1]) }

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

                Log.d(TAG, "getFilteredTags: $query")

                val filteredTags = (if (query.isEmpty()) api.getAllTags().string else api.getFilteredTags(query).string)
                    .split(", ")
                    .map { it.split(" = ") }
                    .map { Tag(it[0], it[1]) }

                Log.d(TAG, "getFilteredTags AZZ: $filteredTags")

                val refreshedTags = appState.value.tags.toMutableList()
                refreshedTags.forEach { tag ->
                    val isClickedTag = clickedTag.type == tag.type && clickedTag.value == tag.value
                    val shouldBeEnabled = filteredTags.any { it.type == tag.type && it.value == tag.value }
//                    val shouldBeEnabled = filteredTags.firstOrNull { it.type == tag.type && it.value == tag.value } != null

                    when {
                        isClickedTag -> if (clickedTag.state == SELECTED) tag.state = ENABLED else tag.state = SELECTED
                        shouldBeEnabled -> if (tag.state != SELECTED) tag.state = ENABLED
                        else -> if (selectedTags.none { it.type == tag.type }) tag.state = DISABLED
//                        else -> if (tag.type != clickedTag.type) tag.state = DISABLED
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
}