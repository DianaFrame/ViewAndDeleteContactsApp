package com.example.data.storage

import android.content.Context
import com.example.data.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsStorage {

    fun loadContacts(context: Context): Flow<List<Contact>>


}