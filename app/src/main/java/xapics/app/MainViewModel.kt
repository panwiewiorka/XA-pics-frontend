package xapics.app

import android.util.Log
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
import xapics.app.data.PicsApi.Companion.BASE_URL
import java.io.File
import java.io.IOException
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

    fun updateTopBarCaption(caption: String) {
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

    fun getPicsList(query: String) {
//        val caption =
//            (year?.toString() ?: "") + (roll ?: "") + if(film != null) "film: $film " else "" + if(tag != null) "#$tag " else "" +
//                    if(description != null) "\"$description\" " else ""

//        val query =
//        updateTopBarCaption(caption)
        updateLoadingState(true)
        clearPicsList()
        viewModelScope.launch {
            try {
                _appState.update { it.copy(
                    picsList = api.getPicsList(query),
                    picIndex = 1,
                    isLoading = false
                )}
                Log.d(TAG, "getPicsList: $query")
            } catch (e: Exception) {
                Log.e(TAG, "getPicsList: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun search(query: String) {
        clearPicsList()
        viewModelScope.launch {
            try {
                updateLoadingState(true)
//                api.search(query)
                _appState.update { it.copy(
                    picsList = api.search(query),
                    picIndex = 1,
                    isLoading = false
                )}
                updateTopBarCaption("\"$query\"")

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

    private fun addToCaptionList(caption: String) {
        _appState.update { it.copy(
            captionsList = appState.value.captionsList + caption
        )}
    }

    fun removeLastCaptionFromList() {
        _appState.update { it.copy(
            captionsList = appState.value.captionsList.drop(1)
        )}
    }

    fun replaceTopBarCaptionWithPrevious() {
        val list = appState.value.captionsList
        if (list.isNotEmpty()) {
            updateTopBarCaption(list.last())
            removeLastCaptionFromList()
        }
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
}