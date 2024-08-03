package xapics.app.domain.useCases

import xapics.app.Pic
import xapics.app.data.PicsApi
import xapics.app.data.db.StateSnapshot
import xapics.app.data.db.XaDao

class GetRandomPicUseCase(
    private val dao: XaDao,
    private val api: PicsApi,
    ) {
    suspend operator fun invoke(): Pic {

        var pic = api.getRandomPic()
        val snapshot = dao.loadSnapshot()

        if (snapshot.pic != null && snapshot.pic == pic) {
            pic = api.getRandomPic()
        }

        dao.saveSnapshot(
            StateSnapshot(
                id = snapshot.id,
                pic = pic
            )
        )

        return pic
    }
}