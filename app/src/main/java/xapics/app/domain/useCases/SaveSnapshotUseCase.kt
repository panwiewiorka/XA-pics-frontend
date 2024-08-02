package xapics.app.domain.useCases

import xapics.app.Pic
import xapics.app.data.db.StateHistory
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SaveSnapshotUseCase(
    private val dao: XaDao
) {
    suspend operator fun invoke(picsList: List<Pic>, pic: Pic?, picIndex: Int?, topBarCaption: String?) {
        val caption = topBarCaption ?: dao.getTopBarCaption() ?: "XA pics"
        val lastRow = dao.getLatestId() ?: 0
//        val thePic = pic ?: dao.loadSnapshot().pic
//        val thePicIndex = picIndex ?: dao.loadSnapshot().picIndex ?: 0

        dao.saveSnapshot(
            StateHistory(
                id = lastRow + 1,
                picsList = picsList,
                pic = pic,
                picIndex = picIndex,
                topBarCaption = caption.transformTopBarCaption()
            )
        )
    }
}