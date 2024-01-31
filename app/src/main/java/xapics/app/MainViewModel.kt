package xapics.app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import xapics.app.ui.auth.AuthState
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

//    var authState by mutableStateOf(AuthState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    init {
        authenticate()
//        getUserInfo() // TODO needed?
//        getPicsList(2020)
        getRollsList()
    }

    /*
    fun onAuthEvent(event: AuthUiEvent) {
        when(event) {
            is AuthUiEvent.SignInUsernameChanged -> {
                authState = authState.copy(signInUsername = event.value)
            }
            is AuthUiEvent.SignInPasswordChanged -> {
                authState = authState.copy(signInPassword = event.value)
            }
            is AuthUiEvent.SignIn -> {
                signIn()
            }
            is AuthUiEvent.SignUpUsernameChanged -> {
                authState = authState.copy(signUpUsername = event.value)
            }
            is AuthUiEvent.SignUpPasswordChanged -> {
                authState = authState.copy(signUpPassword = event.value)
            }
            is AuthUiEvent.SignUp -> {
                signUp()
            }
        }
    }


    private fun signUp() {
        viewModelScope.launch {
            authState = authState.copy(isLoading = true)
            val result = repository.signUp(
                username = authState.signUpUsername,
                password = authState.signUpPassword
            )
            resultChannel.send(result)
            authState = authState.copy(isLoading = false)
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            authState = authState.copy(isLoading = true)
            val result = repository.signIn(
                username = authState.signInUsername,
                password = authState.signInPassword
            )
            resultChannel.send(result)
            authState = authState.copy(isLoading = false)
        }
    }
     */

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
                _appState.update { it.copy(
                    userId = 0,
                    userCollections = null,
                ) }
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
                val result = repository.authenticate(::updateUserId)
                resultChannel.send(result)
                updateLoadingState(false)
            } catch (e: Exception) {
                Log.e(TAG, "authenticate(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun logOut() {
        repository.logOut()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "getUserInfo: start")
                updateLoadingState(true)
                val result = repository.getUserInfo(::updateUserId, ::updateUserCollections)
                Log.d(TAG, "getUserInfo: after API")
                resultChannel.send(result)
                Log.d(TAG, "getUserInfo: after result")
                updateLoadingState(false)
                Log.d(TAG, "getUserInfo: success")
            } catch (e: Exception) {
                Log.e(TAG, "getUserInfo(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updateUserId(userId: Int?) {
        _appState.update { it.copy(
            userId = userId,
        ) }
    }

    private fun updateUserCollections(userCollections: List<Thumb>?) {
        _appState.update { it.copy(
            userCollections = userCollections,
        ) }
    }

    fun editCollection(collection: String, picId: Int) {
        viewModelScope.launch {
            val result = repository.editCollection(
                collection = collection,
                picId = picId
            )
            when (result) {
                is AuthResult.Authorized -> {
                    Log.d(TAG, "Pic $picId added to $collection")
                    getPicCollections(picId) // TODO locally (check success first). Same everywhere ^v
                }
                is AuthResult.Conflicted -> TODO()
                is AuthResult.Unauthorized -> Log.d(TAG, "401 unauthorized")
                is AuthResult.UnknownError -> Log.d(TAG, "Unknown error")
            }
        }
    }

    fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?) {
        // if renamedTitle == null -> delete collection
        viewModelScope.launch {
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
        }
    }

    fun getCollection(collection: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getCollection(collection, ::updatePicsList, ::updateTopBarCaption)
        }
    }

//    fun getAllCollections() {
//        viewModelScope.launch {
//            repository.getAllCollections(::updateAllCollections)
//        }
//    }

//    private fun updateAllCollections(userCollections: List<Thumb>?) {
//        _appState.value = appState.value.copy(
//            userCollections = userCollections,
//        )
//    }

    fun updateCollectionToSaveTo(collection: String) {
        val newCollection = appState.value.userCollections?.firstOrNull { it.title == collection } == null
        _appState.update { it.copy(
            collectionToSaveTo = collection,
            ) }
        if(newCollection) {
            _appState.update { it.copy(
                userCollections = appState.value.userCollections?.plus(Thumb(collection, appState.value.pic!!.imageUrl)),
            )}
        }
    }

    private fun updatePicsList(picsList: List<Pic>) {
        _appState.update { it.copy(
            picsList = picsList,
            picIndex = 1,
        )}
    }

    fun updateTopBarCaption(caption: String) {
        _appState.update { it.copy(
            topBarCaption = caption
        )}
    }

    private fun getPicCollections(picId: Int) {
        viewModelScope.launch {
            repository.getPicCollections(picId, ::updatePicCollections)
        }
    }

    private fun updatePicCollections(picCollections: List<String>) {
        _appState.update { it.copy(
            picCollections = picCollections,
        )}
    }

    fun getFilmsList() {
        CoroutineScope(Dispatchers.IO).launch {
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
        CoroutineScope(Dispatchers.IO).launch {
            api.postFilm(
                isNewFilm,
                film.filmName,
                film.iso ?: 0,
                film.type,
                film.xpro,
                film.expired
            )
        }
    }

    fun getRollsList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    filmsList = api.getFilmsList(),
                    rollsList = api.getRollsList(),
                    rollThumbnails = api.getRollThumbnails(),
                    isLoading = false
                )}
            } catch (e: Exception) {
                Log.e(TAG, "getRollsList(): ", e)
                updateLoadingState(false)
            }
        }
    }

    fun postRoll(isNewRoll: Boolean, roll: Roll, ) {
        CoroutineScope(Dispatchers.IO).launch {
            api.postRoll(
                isNewRoll,
                roll.title,
                roll.film,
                roll.nonXa
            )
        }
    }

    fun getPicsList(year: Int? = null, roll: String? = null, film: String? = null, tag: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    picsList = api.getPicsList(year?.toString(), roll, film, tag),
                    picIndex = 1,
                    isLoading = false
                )}
                updateTopBarCaption((year?.toString() ?: "") + (roll ?: "") + if(film != null) "$film film" else "" + if(tag != null) "#$tag" else "")

            } catch (e: Exception) {
                Log.e(TAG, "getPicsList: ", e)
                updateLoadingState(false)
            }
        }
    }

    fun updatePicState(picIndex: Int) {
        Log.d(TAG, "updatePicState picIndex: ${appState.value.picsList?.get(picIndex)!!.id}")
        getPicCollections(appState.value.picsList?.get(picIndex)!!.id)
        _appState.update {
            it.copy(
                pic = appState.value.picsList?.get(picIndex),
                picIndex = picIndex
            )
        }
    }

//    fun getPic(picType: PicType) {
//        when(picType) {
//            FIRST -> _appState.value = appState.value.copy(
//                pic = appState.value.picsList?.first(),
//                picIndex = 0
//            )
//            PREV -> {
//                _appState.value = appState.value.copy(
//                    pic = appState.value.picsList?.get(appState.value.picIndex!! - 1),
//                    picIndex = appState.value.picIndex!! - 1
//                )
//            }
//            NEXT -> {
//                _appState.value = appState.value.copy(
//                    pic = appState.value.picsList?.get(appState.value.picIndex!! + 1),
//                    picIndex = appState.value.picIndex!! + 1
//                )
//            }
//        }
//    }

    fun selectFilmToEdit(film: Film?) {
        _appState.update { it.copy( filmToEdit = film) }
    }

    fun editFilmField(
        filmName: String? = null,
        iso: Int? = null,
        type: FilmType? = null,
        xpro: Boolean? = null,
        expired: Boolean? = null,
    ) {
        val film = Film(
            filmName ?: appState.value.filmToEdit!!.filmName,
            iso ?: appState.value.filmToEdit!!.iso,
            type ?: appState.value.filmToEdit!!.type,
            xpro ?: appState.value.filmToEdit!!.xpro,
            expired ?: appState.value.filmToEdit!!.expired,
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
        nonXa: Boolean? = null,
    ) {
        Log.d(TAG, "editRollField: film = $film")
        val roll = Roll(
            title ?: appState.value.rollToEdit!!.title,
            film ?: appState.value.rollToEdit!!.film,
            nonXa ?: appState.value.rollToEdit!!.nonXa,
        )
        _appState.update { it.copy(rollToEdit = roll) }
    }

    fun updateRollsListState(list: List<Roll>) {
        _appState.update { it.copy( rollsList = list) }
    }

    private suspend fun tryUploadImage(rollTitle: String, file: File): Boolean {
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
        }
    }

    fun uploadImage(rollTitle: String, file: File) {
        CoroutineScope(Dispatchers.IO).launch {
        //viewModelScope.launch {
            tryUploadImage(rollTitle, file)
        }
    }

    fun getRandomPic() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateLoadingState(true)
                _appState.update { it.copy(
                    pic = api.getRandomPic(),
                    isLoading = false
                )}

            } catch (e: Exception) {
                Log.e("MainViewModel", "getRandomPic: ", e)
                updateLoadingState(false)
            }
        }
    }
}