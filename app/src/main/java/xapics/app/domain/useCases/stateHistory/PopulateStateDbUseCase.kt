package xapics.app.domain.useCases.stateHistory

import xapics.app.Pic
import xapics.app.data.PicsApi
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class PopulateStateDbUseCase(
    private val dao: XaDao,
    private val api: PicsApi,
) {
    suspend operator fun invoke(): Pic {
        val pic = api.getRandomPic()
        dao.clearSnapshotsTable()
        dao.populateDB(
            StateSnapshot(pic = pic)
        )
        return pic
    }
}