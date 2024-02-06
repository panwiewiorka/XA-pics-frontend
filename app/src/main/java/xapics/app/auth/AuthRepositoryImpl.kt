package xapics.app.auth

import android.content.SharedPreferences
import android.util.Log
import retrofit2.HttpException
import xapics.app.Thumb
import xapics.app.Pic
import xapics.app.TAG
import xapics.app.data.PicsApi

class AuthRepositoryImpl(
    private val api: PicsApi,
    private val prefs: SharedPreferences
): AuthRepository {

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
            prefs.edit()
                .putString("jwt", response.token)
                .apply()
            val userId = response.userId
            Log.d(TAG, "signIn: userId = $userId")
            AuthResult.Authorized(userId) as AuthResult<Unit>
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

    override suspend fun authenticate(updateUserId: (Int?) -> Unit): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
            val userId = api.getUserId("Bearer $token")
//            api.authenticate("Bearer $token")
            Log.d(TAG, "authenticate: userId = $userId")
            updateUserId(userId.toIntOrNull())
            AuthResult.Authorized(userId) as AuthResult<Unit>
        } catch (e: HttpException) {
            Log.e(TAG, "authenticate: ", e)
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override fun logOut(): AuthResult<Unit> {
        prefs.edit()
            .putString("jwt", null)
            .apply()
        return AuthResult.Unauthorized()
    }

    override suspend fun getUserInfo(updateUserId: (Int?) -> Unit, updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
//            val userId = api.getUserId("Bearer $token")
            val userCollections = api.getAllCollections("Bearer $token")
//            updateUserId(userId.toIntOrNull())
            updateUserCollections(userCollections)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            Log.e(TAG, "getUserInfo: ", e)
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null)
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
            val token = prefs.getString("jwt", null)
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

    override suspend fun getCollection(collection: String, updatePicsList: (List<Pic>) -> Unit, updateTopBarCaption: (String) -> Unit): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null)
            val picsList = api.getCollection("Bearer $token", collection).reversed()
            updatePicsList(picsList)
            updateTopBarCaption(collection)
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
            val token = prefs.getString("jwt", null)
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
}