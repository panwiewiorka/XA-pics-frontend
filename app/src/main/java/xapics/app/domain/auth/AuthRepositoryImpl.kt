package xapics.app.domain.auth

import android.util.Log
import retrofit2.HttpException
import xapics.app.TAG
import xapics.app.Thumb
import xapics.app.data.EncryptedSharedPrefs
import xapics.app.data.PicsApi
import xapics.app.data.auth.AuthRequest
import xapics.app.data.auth.AuthResult
import xapics.app.data.db.Caption
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class AuthRepositoryImpl(
    private val api: PicsApi,
    private val dao: XaDao,
    private val cryptoPrefs: EncryptedSharedPrefs
): AuthRepository {

    /** AUTH */

    override suspend fun signUp(username: String, password: String): AuthResult<String?> {
        return try {
            api.signUp(
                request = AuthRequest(username, password)
            )
            signIn(username, password)
        } catch (e: HttpException) {
            Log.e(TAG, "signUp: ", e)
            if (e.code() == 409) {
                AuthResult.Conflicted(e.response()?.errorBody()?.string())
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun signIn(username: String, password: String): AuthResult<String?> {
        return try {
            val response = api.signIn(
                request = AuthRequest(username, password)
            )
            cryptoPrefs.putString("accessToken", response.accessToken)
            cryptoPrefs.putString("refreshToken", response.refreshToken)

            AuthResult.Authorized(response.userName)

        } catch (e: HttpException) {
            Log.e(TAG, "signIn: ", e)
            if (e.code() == 409) {
                AuthResult.Conflicted(e.response()?.errorBody()?.string())
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun refreshTokens(): AuthResult<String?> {
        return try {
            val refreshToken = cryptoPrefs.getString("refreshToken", null) ?: return AuthResult.Unauthorized()
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

    override suspend fun authenticate(updateUserName: (String?) -> Unit): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
                val userName = api.authenticate("Bearer $token").string
                updateUserName(userName)
                AuthResult.Authorized(userName)
            } catch (e: HttpException) {
                Log.e(TAG, "authenticate: ", e)
                if (e.code() == 401) {
                    AuthResult.Unauthorized()
                } else {
                    AuthResult.UnknownError()
                }
            }
        }
    }

    override suspend fun getUserCollections(updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
                val userCollections = api.getUserCollections("Bearer $token")
                updateUserCollections(userCollections)
                AuthResult.Authorized()
            } catch (e: HttpException) {
                Log.e(TAG, " repo getUserCollections: ", e)
                if (e.code() == 401) {
                    AuthResult.Unauthorized()
                } else {
                    AuthResult.UnknownError()
                }
            }
        }
    }

    override suspend fun runOrRefreshTokensAndRun(func: suspend (String) -> AuthResult<String?>): AuthResult<String?> {
        var token = cryptoPrefs.getString("accessToken", null) ?: return AuthResult.Unauthorized()

        var result = func(token)

        if (result is AuthResult.Unauthorized) {
            result = refreshTokens()
            if (result is AuthResult.Authorized) {
                token = cryptoPrefs.getString("accessToken", null)!!
                result = func(token)
            }
        }
        return result
    }

    override fun logOut(): AuthResult<Unit> {
        cryptoPrefs.putString("accessToken", null)
        cryptoPrefs.putString("refreshToken", null)
        return AuthResult.Unauthorized()
    }


    /** COLLECTIONS */

    override suspend fun editCollection(collection: String, picId: Int): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
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
    }

    override suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
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
    }

    override suspend fun getCollection(collection: String): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
                val picsList = api.getCollection("Bearer $token", collection).reversed()
                val caption = dao.getCaption()

                dao.saveCaption(
                    Caption(
                        id = caption.id + 1,
                        topBarCaption = collection.transformTopBarCaption()
                    )
                )

                dao.updateStateSnapshot(
                    StateSnapshot(
                        id = 1,
                        picsList = picsList
                    )
                )

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

    override suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<String?> {
        return runOrRefreshTokensAndRun { token ->
            try {
                val collectionsList = api.getPicCollections ("Bearer $token", picId)
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

}