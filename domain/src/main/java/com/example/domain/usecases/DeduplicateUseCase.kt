package com.example.domain.usecases

import com.example.domain.Result
import com.example.domain.repository.ContactsRepository

class DeduplicateUseCase(private val contactsRepository: ContactsRepository) {
    suspend fun execute(): Result {
        return contactsRepository.deduplicate()
    }
}