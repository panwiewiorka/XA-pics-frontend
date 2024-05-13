package xapics.app.data.auth

import android.util.Log
import retrofit2.HttpException
import xapics.app.Pic
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.data.EncryptedSharedPrefs
import xapics.app.data.PicsApi

class AuthRepositoryImpl(
    private val api: PicsApi,
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
            cryptoPrefs.putString("accessToken", response.accessToken)
            cryptoPrefs.putString("refreshToken", response.refreshToken)

            AuthResult.Authorized(response.userName) as AuthResult<Unit>

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

    override suspend fun refreshTokens(): AuthResult<Unit> {
        return try {
            val refreshToken = cryptoPrefs.getString("refreshToken", null) ?: return AuthResult.UnknownError()
            val response = api.refreshTokens("Bearer $refreshToken")
            cryptoPrefs.putString("accessToken", response.accessToken)
            cryptoPrefs.putString("refreshToken", response.refreshToken)

            AuthResult.Authorized()

        } catch (e: HttpException) {
            Log.e(TAG, "refreshTokens: ", e)
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun authenticate(updateUserName: (String?) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("accessToken", null) ?: return AuthResult.Unauthorized()
            val userName = api.authenticate("Bearer $token").string
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

    override suspend fun getUserInfo(updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("accessToken", null) ?: return AuthResult.Unauthorized()
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
        cryptoPrefs.putString("accessToken", null)
        cryptoPrefs.putString("refreshToken", null)
        return AuthResult.Unauthorized()
    }


    /** COLLECTIONS */

    override suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit> {
        return try {
            val token = cryptoPrefs.getString("accessToken", null)
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
            val token = cryptoPrefs.getString("accessToken", null)
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
            val token = cryptoPrefs.getString("accessToken", null)
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
            val token = cryptoPrefs.getString("accessToken", null)
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