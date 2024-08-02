package xapics.app.domain.useCases

import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class UpdateTopBarCaptionUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(query: String): String {
        val caption = query.transformTopBarCaption()
        dao.replaceTopBarCaption(caption)
        return caption
    }
}