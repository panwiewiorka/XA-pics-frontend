package xapics.app.domain.useCases

import xapics.app.data.db.XaDao
import xapics.app.data.db.StateHistory

class LoadSnapshotUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(): StateHistory {
        dao.deleteSnapshot()
        //        if (snapshot.pic == null) snapshot.pic = dao.loadSnapshot().pic
        return dao.loadSnapshot()
    }
}