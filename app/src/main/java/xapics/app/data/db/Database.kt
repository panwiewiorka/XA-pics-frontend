package xapics.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [
    XaData::class,
], version = 1, exportSchema = false)
@TypeConverters(PicTypeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDao(): XaDao
}