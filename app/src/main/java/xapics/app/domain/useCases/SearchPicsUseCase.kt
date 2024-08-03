package xapics.app.domain.useCases

import xapics.app.data.PicsApi
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SearchPicsUseCase(
    private val dao: XaDao,
    private val api: PicsApi
) {
    suspend operator fun invoke(query: String){
        val state = dao.loadSnapshot()
        val picsList = api.search(query)

        dao.updateSnapshot(
            StateSnapshot(
                id = state.id + 1,
                picsList = picsList,
                pic = if (picsList.size == 1) picsList[0] else null,
                picIndex = if (picsList.size == 1) 0 else null,
                topBarCaption = query.transformTopBarCaption()
            )
        )
//        dao.saveSnapshot(
//            StateSnapshot(
//                id = state.id + 1,
//                picsList = picsList,
//                pic = state.pic,
//                picIndex = state.picIndex,
//            )
//        )
    }
}