package com.example.roomdatabse

import androidx.lifecycle.LiveData

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    // Function to get all profiles (used when search query is empty)
    fun getAllUserProfiles(): LiveData<List<UserProfile>> {
        // Calls the corresponding function in the DAO
        return userProfileDao.getAllUserProfiles()

    }

    suspend fun insert(userProfile: UserProfile) {
        userProfileDao.insert(userProfile)
    }

    suspend fun update(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    suspend fun delete(userProfile: UserProfile) {
        userProfileDao.delete(userProfile)
    }

    // Function to search/filter profiles based on the query string
    fun searchProfiles(query: String): LiveData<List<UserProfile>> {
        // Calls the corresponding function in the DAO, which executes the SQL query
        return userProfileDao.searchProfiles(query)
    }
}