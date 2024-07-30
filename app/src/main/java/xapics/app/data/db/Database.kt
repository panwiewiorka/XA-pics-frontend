package xapics.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    XaData::class,
], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDao(): XaDao
}