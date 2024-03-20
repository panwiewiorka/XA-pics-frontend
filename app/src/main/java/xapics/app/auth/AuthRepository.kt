package xapics.app.auth

import xapics.app.Film
import xapics.app.Pic
import xapics.app.Roll
import xapics.app.Tag
import xapics.app.Thumb
import java.io.File

interface AuthRepository {
    suspend fun signUp(username: String, password: String): AuthResult<Unit>
    suspend fun signIn(username: String, password: String): AuthResult<Unit>
    suspend fun authenticate(updateUserName: (String?) -> Unit): AuthResult<Unit>
    suspend fun getUserInfo(updateUserName: (String?) -> Unit, updateUserCollections: (List<Thumb>?) -> Unit): AuthResult<Unit>
    fun logOut(): AuthResult<Unit>
    suspend fun editCollection(collection: String, picId: Int): AuthResult<Unit>
    suspend fun renameOrDeleteCollection(collectionTitle: String, renamedTitle: String?): AuthResult<Unit>
    suspend fun getCollection(collection: String, updatePicsList: (List<Pic>) -> Unit): AuthResult<Unit>
    suspend fun getPicCollections(picId: Int, updatePicCollections: (List<String>) -> Unit): AuthResult<Unit>
    suspend fun postFilm(isNewFilm: Boolean, film: Film, getFilmsList: () -> Unit): AuthResult<Unit>
    suspend fun postRoll(isNewRoll: Boolean, roll: Roll, getRollsList: () -> Unit): AuthResult<Unit>
    suspend fun editPic(id: Int, imageUrl: String, year: String, description: String, hashtags: List<Tag>): AuthResult<Unit>
    suspend fun uploadImage(rollTitle: String, description: String, year: String, hashtags: String, file: File, getAllTags: () -> Unit): AuthResult<Unit>
}