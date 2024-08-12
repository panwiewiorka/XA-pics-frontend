package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class GetStateSnapshotUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(): StateSnapshot {
        return dao.getStateSnapshot()
    }
}