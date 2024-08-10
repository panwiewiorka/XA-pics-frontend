package xapics.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface XaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun populateCaptionTable(caption: Caption)

    @Upsert
    suspend fun saveCaption(caption: Caption)

    @Query("SELECT * FROM Caption WHERE id = (SELECT MAX(id) FROM Caption)")
    suspend fun getCaption(): Caption

    @Query("SELECT * from Caption WHERE id = (SELECT MAX(id) FROM Caption)")
    fun getCaptionFlow(): Flow<Caption>

    @Query("DELETE FROM Caption WHERE id = (SELECT MAX(id) FROM Caption)")
    suspend fun deleteCaption()

    @Query("DELETE from Caption")
    suspend fun clearCaptionsTable()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun populateStateTable(stateSnapshot: StateSnapshot)

    @Upsert
    suspend fun updateStateSnapshot(stateSnapshot: StateSnapshot)

    @Query("SELECT * FROM StateSnapshot WHERE id = 1")
    suspend fun getStateSnapshot(): StateSnapshot

    @Query("SELECT * from StateSnapshot WHERE id = 1")
    fun getStateSnapshotFlow(): Flow<StateSnapshot>

    @Query("DELETE from StateSnapshot")
    suspend fun clearStateSnapshot()

}