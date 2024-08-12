package xapics.app.domain.auth

import xapics.app.Thumb
import xapics.app.data.auth.AuthResult

interface AuthRepository {
    suspend fun signUp(username: String, password: String): AuthResult<String?>
    suspend fun signIn(username: String, password: String): AuthResult<String?>
    suspend fun refreshTokens(): AuthResult<String?>
    suspend fun authenticate(): AuthResult<String?>
    suspend fun getUserCollections(updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<String?>
    suspend fun runOrRefreshTokensAndRun(func: suspend (String) -> AuthResult<String?>): AuthResult<String?>
    fun logOut(): AuthResult<Unit>
    suspend fun editCollection(collection: String, picId: Int): AuthResult<String?>
    suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<String?>
    suspend fun getCollection(collection: String): AuthResult<String?>
    suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<String?>
}