package com.ancientones.squadup.database.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Message (
    @DocumentId
    val id: String,
    val from: String,
    val from_id: DocumentId,
    val message: String,
    val timestamp: ServerTimestamp
    ){}
