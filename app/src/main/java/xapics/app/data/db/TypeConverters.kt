package xapics.app.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import xapics.app.Pic
import xapics.app.Tag

class PicTypeConverter {
    val gson = Gson()

    @TypeConverter
    fun tagsListToString(tagsList: List<Tag>): String {
        return gson.toJson(tagsList)
    }

    @TypeConverter
    fun stringToTagsList(tagsListString: String): List<Tag> {
        val objectType = object : TypeToken<List<Tag>>() {}.type
        return gson.fromJson(tagsListString, objectType)
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