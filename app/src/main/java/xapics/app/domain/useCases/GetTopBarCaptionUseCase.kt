package xapics.app.domain.useCases

import kotlinx.coroutines.flow.Flow
import xapics.app.data.db.XaDao

class GetTopBarCaptionUseCase(
    private val dao: XaDao
) {
    operator fun invoke(): Flow<String> {
        return dao.getTopBarCaptionFlow()
    }
}