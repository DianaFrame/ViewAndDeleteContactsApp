package com.example.data.repository

import android.content.Context
import com.example.data.model.Contact
import com.example.data.storage.ContactsStorage
import com.example.domain.model.ContactsItem
import com.example.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactsRepositoryImpl(
    private val storage: ContactsStorage,
    private val context: Context
) : ContactsRepository {

    override fun getContacts(): Flow<List<ContactsItem>> {
        return storage.loadContacts(context).map { contactsList -> contactsList.map { it.toContactsItem() } }
    }

}


private fun Contact.toContactsItem(): ContactsItem {
    return ContactsItem(
        id = this.id,
        phone = this.phone,
        name = this.name,
        photo = this.photo?.path
    )
}

