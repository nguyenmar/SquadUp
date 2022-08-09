package com.ancientones.squadup.database.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Message (
    @DocumentId
    var id: String = "",
    var from: String = "",
    var from_id: String = "",
    var message: String? = "",
    var imageUrl: String = "",
    @ServerTimestamp
    var timestamp: Date = Date()
    ){
}
