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
    suspend fun populateDB(stateSnapshot: StateSnapshot)

    @Upsert
    suspend fun saveSnapshot(stateSnapshot: StateSnapshot)

    @Query("SELECT * FROM StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    suspend fun loadSnapshot(): StateSnapshot

    @Query("SELECT * from StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    fun getSnapshotFlow(): Flow<StateSnapshot>

    @Query("DELETE FROM StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    suspend fun deleteSnapshot()

    @Query("DELETE from StateSnapshot")
    suspend fun clearSnapshotsTable()
}