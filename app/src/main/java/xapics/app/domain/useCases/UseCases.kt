package xapics.app.domain.useCases

data class UseCases(
    val updateTopBarCaption: UpdateTopBarCaptionUseCase,
    val loadSnapshot: LoadSnapshotUseCase,
    val saveSnapshot: SaveSnapshotUseCase,
    val getTopBarCaption: GetTopBarCaptionUseCase,
    val updatePicUseCase: UpdatePicUseCase,
)
