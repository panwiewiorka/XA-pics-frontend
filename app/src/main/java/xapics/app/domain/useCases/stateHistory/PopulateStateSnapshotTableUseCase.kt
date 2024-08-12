package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class PopulateStateSnapshotTableUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke() {
        dao.clearStateSnapshot()
        dao.populateStateTable(StateSnapshot())
    }
}