package com.example.roomdatabse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserProfileRepository

    init {
        val userProfileDao = UserDatabase.getDatabase(application).userProfileDao()
        repository = UserProfileRepository(userProfileDao)
    }

    // Retain the function to get all profiles, which is likely used when the search query is empty.
    fun getAllUserProfiles(): LiveData<List<UserProfile>> {
        return repository.getAllUserProfiles()
    }

    fun insertUserProfile(userProfile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(userProfile)
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(userProfile)
        }
    }

    fun deleteUserProfile(userProfile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(userProfile)
        }
    }

    /**
     * Searches for user profiles whose name or email contains the given query string.
     * If the query is empty, it returns all profiles.
     */
    fun searchUserProfiles(query: String): LiveData<List<UserProfile>> {
        // We use the repository function to delegate the search query to the DAO.
        // It's essential that the repository/DAO handles returning the full list
        // when the query is empty or short enough.
        return repository.searchProfiles(query)
    }
}