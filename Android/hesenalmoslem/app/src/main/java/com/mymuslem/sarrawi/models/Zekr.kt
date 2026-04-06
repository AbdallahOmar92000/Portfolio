package com.mymuslem.sarrawi.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "zekr",
            foreignKeys = [ForeignKey(entity = Letters::class, childColumns = ["Name_ID"], parentColumns = ["ID"])])
data class Zekr(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ID_zekr")
    var ID_zekr:Int?=null,
    @ColumnInfo("Name_ID")
    var Name_ID:Int?=null,
    @ColumnInfo("Description")
    var Description:String?=null,
    @ColumnInfo("Discription_Filter")
    var Discription_Filter:String?=null,
    @ColumnInfo("hint")
    var hint:String?=null,
    @ColumnInfo("couner")
    var couner:String?=null,
    @ColumnInfo("Category")
    var Category:String?=null
)