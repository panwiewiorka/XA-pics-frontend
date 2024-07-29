package xapics.app.domain

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb

interface PicsRepository {
    suspend fun getRollThumbs(): List<Thumb>
    suspend fun search(query: String): List<Pic>
    suspend fun getRandomPic(): Pic
    suspend fun getAllTags(): List<Tag>
    suspend fun getFilteredTags(query: String): List<Tag>
}