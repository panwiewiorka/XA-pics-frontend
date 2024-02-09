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
import xapics.app.Thumb
import xapics.app.Film
import xapics.app.FilmType
import xapics.app.Pic
import xapics.app.Roll
import xapics.app.TheString
import xapics.app.auth.AuthRequest
import xapics.app.auth.TokenResponse

interface PicsApi {

    @POST("signup")
    suspend fun signUp(
        @Body request: AuthRequest
    )

    @POST("signin")
    suspend fun signIn(
        @Body request: AuthRequest
    ): TokenResponse

    @GET("authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )

//    @GET("profile")
//    suspend fun getUserId(
//        @Header("Authorization") token: String
//    ): String

    @GET("profile")
    suspend fun getUserName(
        @Header("Authorization") token: String
    ): TheString

    @GET("pic-collections")
    suspend fun getPicCollections(
        @Header("Authorization") token: String,
        @Query("picid") picId: Int
    ): List<String>

    @GET("all-collections")
    suspend fun getAllCollections(
        @Header("Authorization") token: String,
    ): List<Thumb>

//    @FormUrlEncoded
//    @POST("collections")
//    suspend fun createCollection(
//        @Header("Authorization") token: String,
//        @Field("title") title: String,
//    )

    @FormUrlEncoded
    @POST("collection")
    suspend fun editCollection(
//        @Field("user") user: String,
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
//        @Field("user") user: String,
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

    @GET("picslist")
    suspend fun getPicsList(
        @Query("year") year: String?,
        @Query("roll") roll: String?,
        @Query("film") film: String?,
        @Query("tag") tag: String?,
        @Query("description") description: String?,
    ): List<Pic>

    @GET("films")
    suspend fun getFilmsList(): List<Film>

    @FormUrlEncoded
    @POST("films")
    suspend fun postFilm(
        @Field("isNewFilm") isNewFilm: Boolean,
        @Field("filmName") filmName: String,
        @Field("iso") iso: Int,
        @Field("type") type: FilmType,
        @Field("xpro") xpro: Boolean,
        @Field("expired") expired: Boolean,
    )

    @GET("rolls")
    suspend fun getRollsList(): List<Roll>

    @GET("rollthumbs")
    suspend fun getRollThumbnails(): List<Thumb>

    @FormUrlEncoded
    @POST("rolls")
    suspend fun postRoll(
        @Field("isNewRoll") isNewFilm: Boolean,
        @Field("title") title: String,
        @Field("film") film: String,
        @Field("nonXa") nonXa: Boolean,
    )

    @POST("file")
    @Multipart
    suspend fun uploadImage(
        @Part metadataPath: MultipartBody.Part,
//        @Part metadataName: MultipartBody.Part,
        @Part image: MultipartBody.Part
    )

    companion object {
        const val BASE_URL = "http://192.168.0.87:8080"
    }
}