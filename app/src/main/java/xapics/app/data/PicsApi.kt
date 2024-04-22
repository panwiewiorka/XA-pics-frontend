package xapics.app.data

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import xapics.app.Pic
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

    @GET("profile")
    suspend fun authenticate(
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

    @GET("rollthumbs")
    suspend fun getRollThumbnails(): List<Thumb>

    @GET("tags")
    suspend fun getAllTags(): TheString

    @GET("filteredtags")
    suspend fun getFilteredTags(
        @Query("query") query: String
    ): TheString


    companion object {
        const val BASE_URL = "https://xapics.fijbar.com/v1/"
//        const val BASE_URL = "http://192.168.0.87:8080/v1/"
    }
}