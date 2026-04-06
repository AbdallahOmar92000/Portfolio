package com.mymuslem.sarrawi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "one")
data class Letters(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ID")
    var ID:Int?=null,
    @ColumnInfo("Name")
    var Name:String?=null,
    @ColumnInfo(name = "Fav")
    var Fav: Int?=null ,

    @ColumnInfo("Name_Filter")
    var Name_Filter:String?=null
)
