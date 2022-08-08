package com.ancientones.squadup.database.models

import com.google.firebase.firestore.DocumentId

data class Chat (
    @DocumentId
    val id: String = "",
    val title: String = ""
    ){}


