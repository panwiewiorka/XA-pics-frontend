package xapics.app

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import xapics.app.PicType.*
import xapics.app.data.PicsApi
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val TAG = "mytag"

@HiltViewModel
class MainViewModel @Inject constructor (
    private val api: PicsApi
): ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()


    init {
//        getPicsList(2020)
        getRollsList()
    }

    fun getFilmsList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _state.value = state.value.copy(isLoading = true)
                _state.value = state.value.copy(
                    filmsList = api.getFilmsList(),
                    isLoading = false
                )
//                Log.d(TAG, state.value.filmsList.toString())

            } catch (e: Exception) {
                Log.e(TAG, "getFilmsList: ", e)
                _state.value = state.value.copy(isLoading = false)
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
                _state.value = state.value.copy(isLoading = true)
                _state.value = state.value.copy(
                    filmsList = api.getFilmsList(),
                    rollsList = api.getRollsList(),
                    rollThumbnails = api.getRollThumbnails(),
                    isLoading = false
                )
//                Log.d(TAG, "getRollsList: ${state.value.rollsList}")

            } catch (e: Exception) {
                Log.e(TAG, "getRollsList: ", e)
                _state.value = state.value.copy(isLoading = false)
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

    fun getPicsList(year: Int? = null, roll: String? = null, film: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _state.value = state.value.copy(isLoading = true)
                _state.value = state.value.copy(
                    picsList = api.getPicsList(year?.toString(), roll, film),
                    picIndex = 1,
                    isLoading = false
                )

            } catch (e: Exception) {
                Log.e("MainViewModel", "getPicsList: ", e)
                _state.value = state.value.copy(isLoading = false)
            }
        }
    }

    fun updatePicState(picIndex: Int) {
        _state.update {
            it.copy(
                pic = state.value.picsList?.get(picIndex),
                picIndex = picIndex
            )
        }
    }

    fun getPic(picType: PicType) {
        when(picType) {
            FIRST -> _state.value = state.value.copy(
                pic = state.value.picsList?.first(),
                picIndex = 0
            )
            PREV -> {
                _state.value = state.value.copy(
                    pic = state.value.picsList?.get(state.value.picIndex!! - 1),
                    picIndex = state.value.picIndex!! - 1
                )
            }
            NEXT -> {
                _state.value = state.value.copy(
                    pic = state.value.picsList?.get(state.value.picIndex!! + 1),
                    picIndex = state.value.picIndex!! + 1
                )
            }
        }
    }

    fun selectFilmToEdit(film: Film?) {
        _state.update { it.copy( filmToEdit = film) }
    }

    fun editFilmField(
        filmName: String? = null,
        iso: Int? = null,
        type: FilmType? = null,
        xpro: Boolean? = null,
        expired: Boolean? = null,
    ) {
        val film = Film(
            filmName ?: state.value.filmToEdit!!.filmName,
            iso ?: state.value.filmToEdit!!.iso,
            type ?: state.value.filmToEdit!!.type,
            xpro ?: state.value.filmToEdit!!.xpro,
            expired ?: state.value.filmToEdit!!.expired,
        )
        _state.update { it.copy(filmToEdit = film) }
    }

    fun updateFilmsListState(list: List<Film>) {
        _state.update { it.copy( filmsList = list) }
    }

    fun selectRollToEdit(roll: Roll?) {
        _state.update { it.copy( rollToEdit = roll) }
    }

    fun editRollField(
        title: String? = null,
        film: String? = null,
        nonXa: Boolean? = null,
    ) {
        Log.d(TAG, "editRollField: film = $film")
        val roll = Roll(
            title ?: state.value.rollToEdit!!.title,
            film ?: state.value.rollToEdit!!.film,
            nonXa ?: state.value.rollToEdit!!.nonXa,
        )
        _state.update { it.copy(rollToEdit = roll) }
    }

    fun updateRollsListState(list: List<Roll>) {
        _state.update { it.copy( rollsList = list) }
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
                _state.value = state.value.copy(isLoading = true)
                _state.value = state.value.copy(
                    pic = api.getRandomPic(),
                    isLoading = false
                )

            } catch (e: Exception) {
                Log.e("MainViewModel", "getRandomPic: ", e)
                _state.value = state.value.copy(isLoading = false)
            }
        }
    }
}