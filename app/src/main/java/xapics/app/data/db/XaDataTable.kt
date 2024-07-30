package xapics.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class XaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
)
