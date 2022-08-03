package com.ancientones.squadup.database.models

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentId

data class Chat (
    val run_id: String,
    val messages: CollectionReference
    ){}


