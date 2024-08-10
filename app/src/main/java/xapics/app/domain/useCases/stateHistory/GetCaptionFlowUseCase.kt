package xapics.app.domain.useCases.stateHistory

import kotlinx.coroutines.flow.Flow
import xapics.app.data.db.Caption
import xapics.app.data.db.XaDao

class GetCaptionFlowUseCase(
    private val dao: XaDao,
) {
    operator fun invoke(): Flow<Caption?> {
        return dao.getCaptionFlow()
    }
}