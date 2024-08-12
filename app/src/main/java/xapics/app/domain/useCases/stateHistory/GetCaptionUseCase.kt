package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.Caption
import xapics.app.data.db.XaDao

class GetCaptionUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(): Caption {
        return dao.getCaption()
    }
}