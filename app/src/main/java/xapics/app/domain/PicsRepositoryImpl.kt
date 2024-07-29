package xapics.app.domain

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb
import xapics.app.data.PicsApi
import xapics.app.toTagsList

class PicsRepositoryImpl(
    private val api: PicsApi
) : PicsRepository {
    override suspend fun getRollThumbs(): List<Thumb> {
//        return try {
            return api.getRollThumbnails().sortedByDescending { roll -> roll.thumbUrl }
//        } catch (e: HttpException) {
//            Log.e(TAG, "getRollThumbs: ", e) // "ERROR ${e.code()}: ${e.message()}"
//            emptyList()
//        }
    }

    override suspend fun search(query: String): List<Pic> {
        return api.search(query)
    }

    override suspend fun getRandomPic(): Pic {
        return api.getRandomPic()
    }

    override suspend fun getAllTags(): List<Tag> {
        return api.getAllTags().string.toTagsList()
    }

    override suspend fun getFilteredTags(query: String): List<Tag> {
        return api.getFilteredTags(query).string.toTagsList()
    }

}