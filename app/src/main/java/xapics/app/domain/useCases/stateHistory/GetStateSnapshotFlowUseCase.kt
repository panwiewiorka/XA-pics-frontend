package xapics.app.domain.useCases.stateHistory

import kotlinx.coroutines.flow.Flow
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class GetStateSnapshotFlowUseCase(
    private val dao: XaDao,
) {
    operator fun invoke(): Flow<StateSnapshot> {
        return dao.getStateSnapshotFlow()
    }
}