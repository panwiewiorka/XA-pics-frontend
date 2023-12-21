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
import xapics.app.PicType.*
import xapics.app.auth.AuthRepository
import xapics.app.auth.AuthResult
import xapics.app.data.PicsApi
import xapics.app.ui.auth.AuthState
import xapics.app.ui.auth.AuthUiEvent
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

    var authState by mutableStateOf(AuthState())

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

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

    private fun authenticate() {
        viewModelScope.launch {
            authState = authState.copy(isLoading = true)
            val result = repository.authenticate()
            resultChannel.send(result)
            authState = authState.copy(isLoading = false)
        }
    }

//    private fun authenticate0() {
//        viewModelScope.launch {
//            authState = authState.copy(isLoading = true)
//            val result = repository.getUserInfo(::gg)
//            resultChannel.send(result)
//            authState = authState.copy(isLoading = false)
//        }
//    }
//
//    private fun gg(userId: Int?) {
//        authState = authState.copy(userId = userId)
//    }


    init {
        authenticate()
//        getPicsList(2020)
        getRollsList()
    }


    fun getFilmsList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _appState.value = appState.value.copy(isLoading = true)
                _appState.value = appState.value.copy(
                    filmsList = api.getFilmsList(),
                    isLoading = false
                )
//                Log.d(TAG, state.value.filmsList.toString())

            } catch (e: Exception) {
                Log.e(TAG, "getFilmsList: ", e)
                _appState.value = appState.value.copy(isLoading = false)
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
                _appState.value = appState.value.copy(isLoading = true)
                _appState.value = appState.value.copy(
                    filmsList = api.getFilmsList(),
                    rollsList = api.getRollsList(),
                    rollThumbnails = api.getRollThumbnails(),
                    isLoading = false
                )
//                Log.d(TAG, "getRollsList: ${state.value.rollsList}")

            } catch (e: Exception) {
                Log.e(TAG, "getRollsList: ", e)
                _appState.value = appState.value.copy(isLoading = false)
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
                _appState.value = appState.value.copy(isLoading = true)
                _appState.value = appState.value.copy(
                    picsList = api.getPicsList(year?.toString(), roll, film, tag),
                    picIndex = 1,
                    isLoading = false
                )

            } catch (e: Exception) {
                Log.e("MainViewModel", "getPicsList: ", e)
                _appState.value = appState.value.copy(isLoading = false)
            }
        }
    }

    fun updatePicState(picIndex: Int) {
        _appState.update {
            it.copy(
                pic = appState.value.picsList?.get(picIndex),
                picIndex = picIndex
            )
        }
    }

    fun getPic(picType: PicType) {
        when(picType) {
            FIRST -> _appState.value = appState.value.copy(
                pic = appState.value.picsList?.first(),
                picIndex = 0
            )
            PREV -> {
                _appState.value = appState.value.copy(
                    pic = appState.value.picsList?.get(appState.value.picIndex!! - 1),
                    picIndex = appState.value.picIndex!! - 1
                )
            }
            NEXT -> {
                _appState.value = appState.value.copy(
                    pic = appState.value.picsList?.get(appState.value.picIndex!! + 1),
                    picIndex = appState.value.picIndex!! + 1
                )
            }
        }
    }

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
                _appState.value = appState.value.copy(isLoading = true)
                _appState.value = appState.value.copy(
                    pic = api.getRandomPic(),
                    isLoading = false
                )

            } catch (e: Exception) {
                Log.e("MainViewModel", "getRandomPic: ", e)
                _appState.value = appState.value.copy(isLoading = false)
            }
        }
    }
}