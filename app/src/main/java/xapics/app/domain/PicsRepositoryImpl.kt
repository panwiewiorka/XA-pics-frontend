package xapics.app.domain

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.TagState
import xapics.app.Thumb
import xapics.app.data.PicsApi
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.toTagsList

class PicsRepositoryImpl(
    private val api: PicsApi,
    private val dao: XaDao,
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
        val tags = api.getAllTags().string.toTagsList()
        val picsList = dao.getStateSnapshot().picsList
        dao.updateStateSnapshot(
            StateSnapshot(
                id = 1,
                picsList = picsList,
                tags = tags
            )
        )
        return tags
    }

    override suspend fun getRandomPic(): Pic {
        return api.getRandomPic()
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

        val picsList = dao.getStateSnapshot().picsList
        dao.updateStateSnapshot(
            StateSnapshot(
                id = 1,
                picsList = picsList,
                tags = refreshedTags
            )
        )

        return refreshedTags
    }

}