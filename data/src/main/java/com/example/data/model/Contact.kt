package com.example.data.model

import android.net.Uri


data class Contact(
    val id: Long,
    val phone: String,
    val name: String,
    val photo: Uri?
)
