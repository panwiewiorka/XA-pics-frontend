package xapics.app.domain.useCases

import xapics.app.Pic
import xapics.app.data.PicsApi
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SearchPicsUseCase(
    private val dao: XaDao,
    private val api: PicsApi
) {
    suspend operator fun invoke(query: String): List<Pic> {
        val state = dao.getSnapshot()
        val picsList = api.search(query)

        dao.saveSnapshot(
            StateSnapshot(
                id = state.id + 1,
                picsList = picsList,
                pic = if (picsList.size == 1) picsList[0] else null,
                picIndex = if (picsList.size == 1) 0 else null,
                topBarCaption = query.transformTopBarCaption()
            )
        )

        return picsList
    }
}