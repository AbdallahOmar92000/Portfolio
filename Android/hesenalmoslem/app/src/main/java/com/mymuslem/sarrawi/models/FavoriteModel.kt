package com.mymuslem.sarrawi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Fav")
data class FavoriteModel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ID")
    var ID:Int?=null,
    @ColumnInfo("Name")
    var Name:String?=null
        )