package xapics.app.data

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import xapics.app.Film
import xapics.app.FilmType
import xapics.app.Pic
import xapics.app.Roll
import xapics.app.TheString
import xapics.app.Thumb
import xapics.app.data.auth.AuthRequest
import xapics.app.data.auth.TokenResponse

interface PicsApi {

    @POST("signup")
    suspend fun signUp(
        @Body request: AuthRequest
    )

    @POST("signin")
    suspend fun signIn(
        @Body request: AuthRequest
    ): TokenResponse

    @GET("refresh-tokens")
    suspend fun refreshTokens(
        @Header("Authorization") refreshToken: String
    ): TokenResponse

    @GET("authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )

    @GET("profile")
    suspend fun getUserName(
        @Header("Authorization") token: String
    ): TheString

    @GET("all-collections")
    suspend fun getUserCollections(
        @Header("Authorization") token: String,
    ): List<Thumb>

    @GET("pic-collections")
    suspend fun getPicCollections(
        @Header("Authorization") token: String,
        @Query("picid") picId: Int
    ): List<String>

    @FormUrlEncoded
    @POST("collection")
    suspend fun editCollection(
        @Header("Authorization") token: String,
        @Field("collection") collection: String,
        @Field("picId") picId: Int,
    )

    @GET("collection")
    suspend fun getCollection(
        @Header("Authorization") token: String,
        @Query("collection") collection: String
    ): List<Pic>

    @FormUrlEncoded
    @POST("rename-delete-collection")
    suspend fun renameOrDeleteCollection(
        @Header("Authorization") token: String,
        @Field("collectionTitle") collectionTitle: String,
        @Field("renamedTitle") renamedTitle: String?,
    )

    @GET("randompic")
    suspend fun getRandomPic(): Pic

    @GET("search")
    suspend fun search(
        @Query("query") query: String
    ): List<Pic>

    @GET("films")
    suspend fun getFilmsList(): List<Film>

    @FormUrlEncoded
    @POST("films")
    suspend fun postFilm(
        @Header("Authorization") token: String,
        @Field("isNewFilm") isNewFilm: Boolean,
        @Field("filmName") filmName: String,
        @Field("iso") iso: Int,
        @Field("type") type: FilmType,
    )

    @GET("rolls")
    suspend fun getRollsList(): List<Roll>

    @GET("rollthumbs")
    suspend fun getRollThumbnails(): List<Thumb>

    @FormUrlEncoded
    @POST("rolls")
    suspend fun postRoll(
        @Header("Authorization") token: String,
        @Field("isNewRoll") isNewRoll: Boolean,
        @Field("title") title: String,
        @Field("film") film: String,
        @Field("xpro") xpro: Boolean,
        @Field("expired") expired: Boolean,
    )

    @GET("tags")
    suspend fun getAllTags(): TheString

    @GET("filteredtags")
    suspend fun getFilteredTags(
        @Query("query") query: String
    ): TheString

    @FormUrlEncoded
    @POST("pic")
    suspend fun editPic(
        @Header("Authorization") token: String,
        @Field("id") id: Int,
        @Field("imageUrl") imageUrl: String,
        @Field("year") year: String,
        @Field("description") description: String,
        @Field("keywords") keywords: String,
        @Field("hashtags") hashtags: String,
    )

    @POST("upload")
    @Multipart
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part roll: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part keywords: MultipartBody.Part,
        @Part year: MultipartBody.Part,
        @Part hashtags: MultipartBody.Part,
        @Part image: MultipartBody.Part
    )

//    @GET("db-backup")
//    suspend fun getBackup(
//        @Header("Authorization") token: String,
//    ): TheString

    @POST("db-backup")
    suspend fun makeBackup(
        @Header("Authorization") token: String,
    )

    @POST("db-restore")
    suspend fun restoreDB(
        @Header("Authorization") token: String,
    )

    companion object {
//        const val BASE_URL = "https://xapics.fijbar.com/v1/"
        const val BASE_URL = "http://192.168.0.87:8080/v1/"
    }
}