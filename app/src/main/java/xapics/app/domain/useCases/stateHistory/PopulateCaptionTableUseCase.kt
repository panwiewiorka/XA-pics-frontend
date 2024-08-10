package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.Caption
import xapics.app.data.db.XaDao

class PopulateCaptionTableUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke() {
        dao.clearCaptionsTable()
        dao.populateCaptionTable(Caption())
    }
}