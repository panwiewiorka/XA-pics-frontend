package xapics.app.domain

import xapics.app.Tag
import xapics.app.TagState
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

    override suspend fun getAllTags(): List<Tag> {
        return api.getAllTags().string.toTagsList()
    }


    override suspend fun getFilteredTags(clickedTag: Tag, tags: List<Tag>): List<Tag> {

        var selectedTags = tags.filter { it.state == TagState.SELECTED }

        selectedTags = if (clickedTag.state == TagState.SELECTED) {
            selectedTags.minus(clickedTag)
        } else {
            selectedTags.plus(clickedTag)
        }

        val query = selectedTags.map {
            "${it.type} = ${it.value}"
        }.toString().drop(1).dropLast(1)

        val filteredTags = if (query.isEmpty()) {
            api.getAllTags().string.toTagsList()
        } else {
            api.getFilteredTags(query).string.toTagsList()
        }

        val refreshedTags = tags.toMutableList()
        refreshedTags.forEach { tag ->
            val isClickedTag = clickedTag.type == tag.type && clickedTag.value == tag.value
            val shouldBeEnabled = filteredTags.any { it.type == tag.type && it.value == tag.value }

            when {
                isClickedTag -> if (clickedTag.state == TagState.SELECTED) tag.state =
                    TagState.ENABLED else tag.state = TagState.SELECTED
                shouldBeEnabled -> if (tag.state != TagState.SELECTED) tag.state =
                    TagState.ENABLED
                else -> if (selectedTags.none { it.type == tag.type }) tag.state =
                    TagState.DISABLED
            }
        }

        return refreshedTags
    }

}