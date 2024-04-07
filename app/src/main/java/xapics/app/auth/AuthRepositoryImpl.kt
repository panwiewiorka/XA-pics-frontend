package xapics.app.auth

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import xapics.app.Film
import xapics.app.Pic
import xapics.app.Roll
import xapics.app.TAG
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.Thumb
import xapics.app.data.EncryptedSharedPrefs
import xapics.app.data.PicsApi
import java.io.File

class AuthRepositoryImpl(
    private val api: PicsApi,
//    private val prefs: SharedPreferences,
    private val cryptoPrefs: EncryptedSharedPrefs
): AuthRepository {

    /** AUTH */

    override suspend fun signUp(username: String, password: String): AuthResult<Unit> {
        return try {
            api.signUp(
                request = AuthRequest(username, password)
            )
            signIn(username, password)
        } catch (e: HttpException) {
            Log.e(TAG, "signUp: ", e)
            (
                if (e.code() == 409) {
                    AuthResult.Conflicted(e.response()?.errorBody()?.string())
                } else {
                    AuthResult.UnknownError()
                }
            ) as AuthResult<Unit>
        }
    }

    override suspend fun signIn(username: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.signIn(
                request = AuthRequest(username, password)
            )
            cryptoPrefs.putString("jwt", response.token)

            val respondedUserName = response.userId
            Log.d(TAG, "signIn: userName = $respondedUserName, token = ${response.token}")
            AuthResult.Authorized(respondedUserName) as AuthResult<Unit>
        } catch (e: HttpException) {
            Log.e(TAG, "signIn: ", e)
            (
                if (e.code() == 409) {
                    AuthResult.Conflicted(e.response()?.errorBody()?.string())
                } else {
                    AuthResult.UnknownError()
                }
                ) as AuthResult<Unit>
        }
    }

    override suspend fun authenticate(updateUserName: (String?) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
            val userName = api.getUserName("Bearer $token").string
            updateUserName(userName)
            AuthResult.Authorized(userName) as AuthResult<Unit>
        } catch (e: HttpException) {
            Log.e(TAG, "authenticate: ", e)
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun getUserInfo(updateUserName: (String?) -> Unit, updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
            val userCollections = api.getUserCollections("Bearer $token")
            updateUserCollections(userCollections)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, " repo getUserInfo: ", e)
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override fun logOut(): AuthResult<Unit> {
        cryptoPrefs.putString("jwt", null)
        return AuthResult.Unauthorized()
    }


    /** COLLECTIONS */

    override suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null)
            api.editCollection("Bearer $token", collection, picId)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null)
            api.renameOrDeleteCollection("Bearer $token", collectionTitle, renamedTitle)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun getCollection(collection: String, updatePicsList: (List<Pic>) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null)
            val picsList = api.getCollection("Bearer $token", collection).reversed()
            updatePicsList(picsList)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null)
            val collectionsList = api.getPicCollections ("Bearer $token", picId)
            Log.d(TAG, "getPicCollections: collectionsList = $collectionsList")
            updatePicCollections(collectionsList)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }


    /** ADMIN */

    override suspend fun postFilm(isNewFilm: Boolean, film: Film, getFilmsList: () -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()

            api.postFilm(
                token = "Bearer $token",
                isNewFilm = isNewFilm,
                filmName = film.filmName.trim(),
                iso = film.iso ?: 0,
                type = film.type)

            getFilmsList()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, "postFilm: ", e)
            if (e.code() == 401 || e.code() == 403) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun postRoll(isNewRoll: Boolean, roll: Roll, getRollsList: () -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()

            api.postRoll(
                token = "Bearer $token",
                isNewRoll = isNewRoll,
                title = roll.title.trim(),
                film = roll.film,
                xpro = roll.xpro,
                expired = roll.expired,
//                nonXa = roll.nonXa,
            )

            getRollsList()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, "postRoll: ", e)
            if (e.code() == 401 || e.code() == 403) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun editPic(
        id: Int,
        imageUrl: String,
        year: String,
        description: String,
        keywords: String,
        hashtags: List<Tag>
    ): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()

            api.editPic(
                token = "Bearer $token",
                id = id,
                imageUrl = imageUrl,
                year = year,
                description = description,
                keywords = keywords,
                hashtags = hashtags.filter { it.state == TagState.SELECTED }.map { it.value }.toString().drop(1).dropLast(1)
            )

            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, "postRoll: ", e)
            if (e.code() == 401 || e.code() == 403) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun uploadImage(
        rollTitle: String,
        description: String,
        keywords: String,
        year: String,
        hashtags: String,
        file: File,
        getAllTags: () -> Unit
    ): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("jwt", null) ?: return AuthResult.Unauthorized()

            api.uploadImage(
                token = "Bearer $token",
                MultipartBody.Part.createFormData("roll", rollTitle),
                MultipartBody.Part.createFormData("description", description),
                MultipartBody.Part.createFormData("keywords", keywords),
                MultipartBody.Part.createFormData("year", year),
                MultipartBody.Part.createFormData("hashtags", hashtags),
                MultipartBody.Part.createFormData("image", file.name, file.asRequestBody())
            )

            file.delete()
            getAllTags()

            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, "uploadImage: ", e)
            if (e.code() == 401 || e.code() == 403) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

}