package xapics.app.domain.useCases

import xapics.app.Pic
import xapics.app.data.PicsApi
import xapics.app.data.db.Caption
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SearchPicsUseCase(
    private val dao: XaDao,
    private val api: PicsApi
) {
    suspend operator fun invoke(query: String): List<Pic> {
        val caption = dao.getCaption()
        val state = dao.getStateSnapshot()
        val searchQuery = query.transformTopBarCaption()

        val picsList = api.search(query)

        dao.saveCaption(
            Caption(
                id = caption.id + 1,
                topBarCaption = searchQuery
            )
        )

        dao.updateStateSnapshot(
            StateSnapshot(
                id = 1,
                picsList = picsList,
                tags = state.tags,
            )
        )

        return picsList
    }
}