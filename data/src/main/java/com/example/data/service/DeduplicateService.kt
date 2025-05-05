package com.example.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.domain.repository.ContactsRepository
import kotlinx.coroutines.runBlocking

class DeduplicateService(
    private val repository: ContactsRepository) : Service() {

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
                    repository.deduplicate()
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
}