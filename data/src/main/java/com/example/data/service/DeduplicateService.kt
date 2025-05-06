package com.example.data.service

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.runBlocking

class DeduplicateService: Service() {

    private var currentProgress = 0
    private var isRunning = false

    private val binder = object : IDeduplicate.Stub() {
        @Throws(RemoteException::class)
        override fun removeDuplicateContacts(): Int {
            if (isRunning) return -1

            return try {
                isRunning = true
                currentProgress = 0
                runBlocking {
                    deleteDuplicates()
                    isRunning = false
                }
                0
            } catch (e: Exception) {
                Log.e("Deduplicate", "Oops...Error starting deduplicate", e)
                isRunning = false
                -2
            }
        }

        override fun isServiceRunning(): Boolean = isRunning

        override fun getProgrss(): Int = currentProgress

    }

    override fun onBind(p0: Intent?): IBinder = binder

    private fun deleteDuplicates() {
        val resolver: ContentResolver = contentResolver
        val contactsMap = mutableMapOf<String, MutableList<Long>>()

        val cursor = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val name = it.getString(nameIndex) ?: ""
                val number = it.getString(numberIndex) ?: ""

                val key = "$name|$number"
                if (!contactsMap.containsKey(key)) {
                    contactsMap[key] = mutableListOf()
                }
                contactsMap[key]?.add(id)

                currentProgress = (it.position * 100 / it.count)
            }
        }

        var deletedCount = 0
        contactsMap.values.forEach { ids ->
            if (ids.size > 1) {
                for (i in 1 until ids.size) {
                    val deleteUri = ContactsContract.RawContacts.CONTENT_URI
                        .buildUpon()
                        .appendQueryParameter(
                            ContactsContract.CALLER_IS_SYNCADAPTER, "true"
                        )
                        .build()

                    val where = "${ContactsContract.RawContacts._ID} = ?"
                    val selectionArgs = arrayOf(ids[i].toString())

                    resolver.delete(deleteUri, where, selectionArgs)
                    deletedCount++
                    currentProgress = 50 + (deletedCount * 50 / (contactsMap.size))
                }
            }
        }
        currentProgress = 100
    }
}