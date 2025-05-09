package com.example.domain.repository

import com.example.domain.model.ContactsItem
import kotlinx.coroutines.flow.Flow


interface ContactsRepository {

    fun getContacts(): Flow<List<ContactsItem>>

}
