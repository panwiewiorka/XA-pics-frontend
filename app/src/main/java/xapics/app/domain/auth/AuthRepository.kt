package xapics.app.domain.auth

import xapics.app.Pic
import xapics.app.Thumb
import xapics.app.data.auth.AuthResult

interface AuthRepository {
    suspend fun signUp(username: String, password: String): AuthResult<Unit>
    suspend fun signIn(username: String, password: String): AuthResult<Unit>
    suspend fun refreshTokens(): AuthResult<Unit>
    suspend fun authenticate(updateUserName: (String?) -> Unit): AuthResult<Unit>
    suspend fun getUserInfo(updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit>
    fun logOut(): AuthResult<Unit>
    suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit>
    suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<Unit>
    suspend fun getCollection(collection: String, updatePicsList: (List<Pic>) -> Unit): AuthResult<Unit>
    suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<Unit>
}