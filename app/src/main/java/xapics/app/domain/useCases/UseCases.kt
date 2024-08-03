package xapics.app.domain.useCases

import xapics.app.domain.useCases.stateHistory.GetSnapshotFlowUseCase
import xapics.app.domain.useCases.stateHistory.LoadSnapshotUseCase
import xapics.app.domain.useCases.stateHistory.PopulateStateDbUseCase
import xapics.app.domain.useCases.stateHistory.UpdateSnapshotUseCase

data class UseCases(
    val populateStateDb: PopulateStateDbUseCase,
    val getRandomPic: GetRandomPicUseCase,
//    val updateTopBarCaption: UpdateTopBarCaptionUseCase,
    val loadSnapshot: LoadSnapshotUseCase,
//    val saveSnapshot: SaveSnapshotUseCase,
    val getSnapshotFlow: GetSnapshotFlowUseCase,
//    val getTopBarCaptionFlow: GetTopBarCaptionFlowUseCase,
//    val updatePic: UpdatePicUseCase,
    val updateSnapshot: UpdateSnapshotUseCase,
    val searchPics: SearchPicsUseCase,
)
