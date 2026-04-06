package com.mymuslem.sarrawi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "azkar_type")
data class ZekerTypes(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var ID: Int,

    @ColumnInfo(name = "Name")
    var Name: String? = null,

    @ColumnInfo(name = "Fav")
    var Fav: Int = 0,

    @ColumnInfo(name = "Name_Filter")
    var Name_Filter: String? = null
)




