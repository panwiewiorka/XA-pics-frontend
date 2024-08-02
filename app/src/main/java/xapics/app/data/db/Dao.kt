package xapics.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xapics.app.Pic

@Dao
interface XaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun populateDB(stateHistory: StateHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSnapshot(stateHistory: StateHistory)

    @Query("SELECT MAX(id) FROM StateHistory")
    suspend fun getLatestId(): Int?

    @Query("UPDATE StateHistory SET topBarCaption = :topBarCaption WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun replaceTopBarCaption(topBarCaption: String)

    @Query("UPDATE StateHistory SET pic = :pic, picIndex = :picIndex WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun replacePic(pic: Pic?, picIndex: Int?)

    @Query("SELECT * FROM StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun loadSnapshot(): StateHistory

    @Query("SELECT topBarCaption from StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    fun getTopBarCaptionFlow(): Flow<String>

    @Query("SELECT topBarCaption from StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun getTopBarCaption(): String?

    @Query("DELETE FROM StateHistory WHERE id = (SELECT MAX(id) FROM StateHistory)")
    suspend fun deleteSnapshot()
}