package xapics.app.domain.useCases

import xapics.app.domain.useCases.stateHistory.GetCaptionFlowUseCase
import xapics.app.domain.useCases.stateHistory.GetCaptionUseCase
import xapics.app.domain.useCases.stateHistory.GetStateSnapshotFlowUseCase
import xapics.app.domain.useCases.stateHistory.GetStateSnapshotUseCase
import xapics.app.domain.useCases.stateHistory.LoadCaptionUseCase
import xapics.app.domain.useCases.stateHistory.PopulateCaptionTableUseCase
import xapics.app.domain.useCases.stateHistory.PopulateStateSnapshotTableUseCase
import xapics.app.domain.useCases.stateHistory.SaveCaptionUseCase
import xapics.app.domain.useCases.stateHistory.UpdateStateSnapshotUseCase

data class UseCases(
    val populateCaptionTable: PopulateCaptionTableUseCase,
    val populateStateSnapshot: PopulateStateSnapshotTableUseCase,
    val loadCaption: LoadCaptionUseCase,
    val getCaptionFlow: GetCaptionFlowUseCase,
    val getCaption: GetCaptionUseCase,
    val saveCaption: SaveCaptionUseCase,
    val getStateSnapshot: GetStateSnapshotUseCase,
    val getStateSnapshotFlow: GetStateSnapshotFlowUseCase,
    val updateStateSnapshot: UpdateStateSnapshotUseCase,
    val searchPics: SearchPicsUseCase,
)
