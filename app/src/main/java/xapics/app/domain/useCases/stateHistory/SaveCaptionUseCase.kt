package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.Caption
import xapics.app.data.db.XaDao
import xapics.app.domain.transformTopBarCaption

class SaveCaptionUseCase(
    private val dao: XaDao,
    ) {
    suspend operator fun invoke(
        replaceExisting: Boolean,
        topBarCaption: String? = null
    ) {
        val savedCaption = dao.getCaption()
        val caption = topBarCaption?.transformTopBarCaption() ?: savedCaption.topBarCaption

        dao.saveCaption(
            Caption(
                id = if (replaceExisting) savedCaption.id else savedCaption.id + 1,
                topBarCaption = caption,
            )
        )
    }
}