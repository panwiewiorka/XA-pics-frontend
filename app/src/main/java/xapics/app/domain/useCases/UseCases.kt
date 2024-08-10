package xapics.app.domain.useCases

import xapics.app.domain.useCases.stateHistory.GetCaptionFlowUseCase
import xapics.app.domain.useCases.stateHistory.GetSnapshotUseCase
import xapics.app.domain.useCases.stateHistory.LoadCaptionUseCase
import xapics.app.domain.useCases.stateHistory.PopulateCaptionTableUseCase
import xapics.app.domain.useCases.stateHistory.SaveCaptionUseCase
import xapics.app.domain.useCases.stateHistory.UpdateStateSnapshotUseCase

data class UseCases(
    val populateCaptionTable: PopulateCaptionTableUseCase,
    val loadCaption: LoadCaptionUseCase,
    val getCaptionFlow: GetCaptionFlowUseCase,
    val saveCaption: SaveCaptionUseCase,
    val getSnapshot: GetSnapshotUseCase,
    val searchPics: SearchPicsUseCase,
    val updateStateSnapshot: UpdateStateSnapshotUseCase,
)
