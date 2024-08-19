package xapics.app.domain.useCases.stateHistory

import xapics.app.Pic
import xapics.app.Tag
import xapics.app.data.db.XaDao

class UpdateStateSnapshotUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(
        picsList: List<Pic>? = null,
        tags: List<Tag>? = null,
    ) {
        val state = dao.getStateSnapshot()

        dao.updateStateSnapshot(
            state.copy(
                picsList = picsList ?: state.picsList,
                tags = tags ?: state.tags,
            )
//            StateSnapshot(
//                id = 1,
//                picsList = picsList ?: state.picsList,
//                tags = tags ?: state.tags,
//            )
        )
    }
}