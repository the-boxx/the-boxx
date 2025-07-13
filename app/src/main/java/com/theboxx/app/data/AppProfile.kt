package com.theboxx.app.data
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.Index
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "app_profiles",
//    foreignKeys = [
//        ForeignKey(
//            entity = App::class,
//            parentColumns = ["packageName"],
//            childColumns = ["appOwnerPackageName"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [Index(value = ["appOwnerPackageName"])]
//)
//data class AppProfile(
//    val profile: Int,
//    val allowOperation: Boolean = true,
//
//    val appOwnerPackageName: String,
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0
//)
