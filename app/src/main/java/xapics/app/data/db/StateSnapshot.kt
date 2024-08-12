package xapics.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import xapics.app.Pic
import xapics.app.Tag

@Entity
data class StateSnapshot(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    @SerializedName("picsList")
    val picsList: List<Pic> = emptyList(),
    @SerializedName("tags")
    val tags: List<Tag> = emptyList(),
//    @SerializedName("pic")
//    val pic: Pic? = null,
//    @SerializedName("picIndex")
//    val picIndex: Int? = null,
//    @SerializedName("topBarCaption")
//    val topBarCaption: String = "XA pics"
)

