package xapics.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import xapics.app.Pic

@Entity
data class XaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    @SerializedName("picsList")
    val picsList: List<Pic> = emptyList(),
    @SerializedName("pic")
    var pic: Pic? = null,
    @SerializedName("picIndex")
    var picIndex: Int? = null,
    @SerializedName("topBarCaption")
    val topBarCaption: String = "XA pics"
)
