package xapics.app.domain.useCases.stateHistory

import xapics.app.data.db.Caption
import xapics.app.data.db.XaDao

class LoadCaptionUseCase(
    private val dao: XaDao,
) {
    suspend operator fun invoke(): Caption {
        dao.deleteCaption()
        //        if (snapshot.pic == null) snapshot.pic = dao.loadSnapshot().pic
        return dao.getCaption()
    }
}