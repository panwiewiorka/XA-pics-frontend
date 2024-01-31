package xapics.app.auth

import xapics.app.Thumb
import xapics.app.Pic

interface AuthRepository {
    suspend fun signUp(username: String, password: String): AuthResult<Unit>
    suspend fun signIn(username: String, password: String): AuthResult<Unit>
    suspend fun authenticate(updateUserId: (Int?) -> Unit): AuthResult<Unit>
    fun logOut(): AuthResult<Unit>
    suspend fun getUserInfo(updateUserId: (Int?) -> Unit, updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit>
    suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit>
    suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<Unit>
    suspend fun getCollection(collection: String, updatePicsList: (List<Pic>) -> Unit, updateTopBarCaption: (String) -> Unit): AuthResult<Unit>
//    suspend fun getAllCollections(updateAllCollections: (List<Thumb>) -> Unit): AuthResult<Unit>
    suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<Unit>
}