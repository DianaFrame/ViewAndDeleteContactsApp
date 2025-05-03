package com.example.domain.usecases

import com.example.domain.model.ContactsItem
import com.example.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow

class GetContactsUseCase(private val contactsRepository: ContactsRepository) {
    fun execute(): Flow<List<ContactsItem>> {
        return contactsRepository.getContacts()
    }
}