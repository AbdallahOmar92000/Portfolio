package com.mymuslem.sarrawi.models

import androidx.room.Embedded

data class LettersTypesModel(
    @Embedded
    var letters: Letters? = null,
)
