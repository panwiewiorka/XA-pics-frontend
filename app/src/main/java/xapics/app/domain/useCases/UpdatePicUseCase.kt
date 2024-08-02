package xapics.app.domain.useCases

import kotlinx.coroutines.delay
import xapics.app.Pic
import xapics.app.data.db.XaDao

class UpdatePicUseCase(
    private val dao: XaDao
) {
    suspend operator fun invoke(pic: Pic?, picIndex: Int?) {
        delay(500)
        dao.replacePic(pic, picIndex)
    }
}