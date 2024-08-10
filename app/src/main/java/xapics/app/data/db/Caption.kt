package xapics.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Caption(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    @SerializedName("topBarCaption")
    val topBarCaption: String = "XA pics"
)
