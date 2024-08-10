package xapics.app.domain

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.Thumb

interface PicsRepository {
    suspend fun getRollThumbs(): List<Thumb>
    suspend fun getAllTags(): List<Tag>
    suspend fun getRandomPic(): Pic
    suspend fun getFilteredTags(clickedTag: Tag, tags: List<Tag>): List<Tag>
}