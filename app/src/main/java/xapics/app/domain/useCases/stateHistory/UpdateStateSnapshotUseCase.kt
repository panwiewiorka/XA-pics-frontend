package xapics.app.domain.useCases.stateHistory

import xapics.app.Pic
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class UpdateStateSnapshotUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(
        picsList: List<Pic>
    ) {
        dao.updateStateSnapshot(
            StateSnapshot(
                id = 1,
                picsList = picsList,
            )
        )
    }
}