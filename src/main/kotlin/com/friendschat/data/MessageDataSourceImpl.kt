package com.friendschat.data

import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
): MessageDataSource {

    private val messages = db.getCollection<Message>()

    override suspend fun getAllMessages(): List<Message> =
        messages.find()
            .descendingSort(Message::timeStamp)
            .toList()

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }
}