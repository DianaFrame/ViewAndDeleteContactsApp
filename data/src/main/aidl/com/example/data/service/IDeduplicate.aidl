// IDeduplicate.aidl
package com.example.data.service;

// Declare any non-default types here with import statements

interface IDeduplicate {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int removeDuplicateContacts();

    boolean isServiceRunning();

    int getProgrss();

}