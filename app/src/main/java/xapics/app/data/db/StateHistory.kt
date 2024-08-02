package xapics.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import xapics.app.Pic

@Entity
data class StateHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("picsList")
    val picsList: List<Pic>,
    @SerializedName("pic")
    var pic: Pic?,
    @SerializedName("picIndex")
    var picIndex: Int?,
    @SerializedName("topBarCaption")
    val topBarCaption: String = "XA pics"
)

