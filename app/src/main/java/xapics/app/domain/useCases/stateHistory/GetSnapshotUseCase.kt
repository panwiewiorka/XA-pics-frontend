package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class GetSnapshotUseCase(
    private val dao: XaDao,
) {
    operator fun invoke(): StateSnapshot {
        return dao.getSnapshot()
    }
}