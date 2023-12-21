package xapics.app.auth

import android.content.SharedPreferences
import android.util.Log
import retrofit2.HttpException
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
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
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
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
            api.authenticate("Bearer $token")
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        }
    }

//    override suspend fun getUserInfo(gg: (Int?) -> Unit): AuthResult<Unit> {
//        return try {
//            val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
//            val userId = api.getUserInfo("Bearer $token")
//            Log.d("mytagg", "getUserInfo: $userId")
//            gg(userId.toIntOrNull())
//            AuthResult.Authorized()
//        } catch (e: HttpException) {
//            if (e.code() == 401) {
//                AuthResult.Unauthorized()
//            } else {
//                AuthResult.UnknownError()
//            }
//        }
//    }
}