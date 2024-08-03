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

    @Query("SELECT MAX(id) FROM StateSnapshot")
    suspend fun getLatestId(): Int?

    @Upsert
    suspend fun updateSnapshot(stateSnapshot: StateSnapshot)

    @Query("SELECT * FROM StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    suspend fun loadSnapshot(): StateSnapshot

    @Query("SELECT * from StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    fun getSnapshotFlow(): Flow<StateSnapshot>

    @Query("DELETE FROM StateSnapshot WHERE id = (SELECT MAX(id) FROM StateSnapshot)")
    suspend fun deleteSnapshot()

    @Query("DELETE from StateSnapshot")
    suspend fun clearSnapshotsTable()



    /*
    @Query("SELECT topBarCaption from StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    fun getTopBarCaptionFlow(): Flow<String>

    @Query("UPDATE StateHistory SET topBarCaption = :topBarCaption WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun replaceTopBarCaption(topBarCaption: String)

    @Query("UPDATE StateHistory SET pic = :pic, picIndex = :picIndex WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun replacePic(pic: Pic?, picIndex: Int?)

    @Query("UPDATE StateHistory SET picsList = :picsList, pic = :pic, picIndex = :picIndex, topBarCaption = :caption WHERE id = :id")
    suspend fun updateSnapshot(id: Int, picsList: List<Pic>, pic: Pic?, picIndex: Int?, caption: String)

    @Query("SELECT topBarCaption from StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun getTopBarCaption(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSnapshot(stateHistory: StateHistory)
     */
}