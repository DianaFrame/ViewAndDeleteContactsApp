package com.example.data.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.example.data.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ContentResolverStorageImpl : ContactsStorage {

    override fun loadContacts(context: Context): Flow<List<Contact>> = flow {

        val contacts = mutableListOf<Contact>()

        val contentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_URI
        )

        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val photoUri = cursor.getString(photoColumn)?.let { Uri.parse(it) }
                val phoneNumbers = getPhoneNumbers(contentResolver, id)
                phoneNumbers.forEach { phoneNumber ->
                    contacts.add(Contact(id, name, phoneNumber, photoUri))
                }
            }
            emit(contacts)
        }
    }

    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: Long): List<String> {
        val phoneNumbers = mutableListOf<String>()

        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use { cursor ->
            val numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val number = cursor.getString(numberColumn)
                phoneNumbers.add(number)
            }
        }

        return phoneNumbers
    }
}