package xapics.app.domain.useCases.stateHistory

import xapics.app.Pic
import xapics.app.data.db.XaDao

class SaveSnapshotUseCase(
    private val dao: XaDao
) {
    suspend operator fun invoke(picsList: List<Pic>, pic: Pic?, picIndex: Int?, topBarCaption: String?) {
        /*
        val caption = topBarCaption ?: dao.getTopBarCaption() ?: "XA pics"
        val lastRow = dao.getLatestId() ?: 0
//        val thePic = pic ?: dao.loadSnapshot().pic
//        val thePicIndex = picIndex ?: dao.loadSnapshot().picIndex ?: 0

        dao.saveSnapshot(
            StateSnapshot(
                id = lastRow + 1,
                picsList = picsList,
                pic = pic,
                picIndex = picIndex,
                topBarCaption = caption.transformTopBarCaption()
            )
        )
         */
    }
}