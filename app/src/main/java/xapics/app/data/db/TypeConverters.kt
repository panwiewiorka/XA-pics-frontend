package xapics.app.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xapics.app.Pic

class PicTypeConverter {
    val gson = Gson()

    @TypeConverter
    fun picToString(pic: Pic?): String {
        return gson.toJson(pic)
    }

    @TypeConverter
    fun stringToPic(picString: String): Pic? {
        val objectType = object : TypeToken<Pic?>() {}.type
        return gson.fromJson(picString, objectType)
    }

    @TypeConverter
    fun picsListToString(picsList: List<Pic>): String {
        return gson.toJson(picsList)
    }

    @TypeConverter
    fun stringToPicsList(picsListString: String): List<Pic> {
        val objectType = object : TypeToken<List<Pic>>() {}.type
        return gson.fromJson(picsListString, objectType)
    }
}