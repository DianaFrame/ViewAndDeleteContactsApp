package com.example.data.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

class DeduplicateClient(private val context: Context) {
    private var service: IDeduplicate? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = IDeduplicate.Stub.asInterface(binder)
            isBound = true
            Log.d("Deduplicate", "Service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
            Log.d("Deduplicate", "Service disconnected")
        }

    }

    fun bindService() {
        val intent = Intent(context, DeduplicateService::class.java)
        intent.action = "com.example.data.service.DeduplicateService"
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    fun removeDuplicateContacts(): Int {
        return try {
            service?.progrss ?: 0
        } catch (e: RemoteException) {
            0
        }
    }

    fun isServiceRunning(): Boolean {
        return try {
            service?.isServiceRunning ?: false
        } catch (e: RemoteException) {
            false
        }
    }
}