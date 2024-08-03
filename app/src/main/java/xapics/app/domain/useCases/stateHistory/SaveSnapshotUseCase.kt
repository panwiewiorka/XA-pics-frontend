package xapics.app.domain.useCases.stateHistory

import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SaveSnapshotUseCase(
    private val dao: XaDao,
    ) {
    suspend operator fun invoke(
        replaceExisting: Boolean,
        picsList: List<Pic>? = null,
        pic: Pic? = null,
        picIndex: Int? = null,
        topBarCaption: String? = null
    ) {
        val snapshot = dao.loadSnapshot()
        val caption = topBarCaption?.transformTopBarCaption() ?: snapshot.topBarCaption

        dao.saveSnapshot(
            StateSnapshot(
                id = if (replaceExisting) snapshot.id else snapshot.id + 1,
                picsList = picsList ?: snapshot.picsList,
                pic = pic ?: snapshot.pic,
                picIndex = picIndex ?: snapshot.picIndex,
                topBarCaption = caption,
            )
        )
    }
}