package com.theboxx.app.data
//
//import androidx.room.TypeConverter
//import kotlinx.serialization.json.Json
//
//class AppProfileListConverter {
//    private val json = Json { ignoreUnknownKeys = true }
//
//    @TypeConverter
//    fun fromAppProfileList(profiles: MutableList<AppProfile>?): String? {
//        if (profiles == null) {
//            return null
//        }
//
//        return json.encodeToString(profiles)
//    }
//
//    @TypeConverter
//    fun toAppProfileList(profilesString: String?): MutableList<AppProfile>? {
//        if (profilesString == null) {
//            return null
//        }
//
//        return json.decodeFromString<MutableList<AppProfile>>(profilesString)
//    }
//}