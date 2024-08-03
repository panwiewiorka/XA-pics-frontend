package xapics.app.domain.useCases

import xapics.app.domain.useCases.stateHistory.GetSnapshotFlowUseCase
import xapics.app.domain.useCases.stateHistory.LoadSnapshotUseCase
import xapics.app.domain.useCases.stateHistory.PopulateStateDbUseCase
import xapics.app.domain.useCases.stateHistory.SaveSnapshotUseCase

data class UseCases(
    val populateStateDb: PopulateStateDbUseCase,
    val getRandomPic: GetRandomPicUseCase,
    val loadSnapshot: LoadSnapshotUseCase,
    val getSnapshotFlow: GetSnapshotFlowUseCase,
    val saveSnapshot: SaveSnapshotUseCase,
    val searchPics: SearchPicsUseCase,
)
