package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class LoadSnapshotUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(): StateSnapshot {
        dao.deleteSnapshot()
        //        if (snapshot.pic == null) snapshot.pic = dao.loadSnapshot().pic
        return dao.loadSnapshot()
    }
}