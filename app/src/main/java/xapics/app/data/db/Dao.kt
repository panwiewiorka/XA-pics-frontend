package xapics.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface XaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun populateSettings(xaData: XaData)

    @Upsert
    suspend fun saveSettings(xaData: XaData)

    @Query("SELECT * from XaData WHERE id = 1")
    fun loadSettings(): XaData
}