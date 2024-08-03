package xapics.app.domain

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb

interface PicsRepository {
    suspend fun getRollThumbs(): List<Thumb>
    suspend fun getAllTags(): List<Tag>
    suspend fun getFilteredTags(query: String): List<Tag>
}