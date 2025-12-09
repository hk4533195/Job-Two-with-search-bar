package com.example.roomdatabse

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabse.databinding.ActivityProfileListBinding

class ProfileListActivity : AppCompatActivity() {

    private lateinit var profileViewModel: UserProfileViewModel
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var binding: ActivityProfileListBinding // View Binding variable

    // Field to track the currently observed LiveData instance for safe removal
    private var currentProfilesLiveData: LiveData<List<UserProfile>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize View Binding
        binding = ActivityProfileListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileViewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)

        profileAdapter = ProfileAdapter()

        // 2. Setup RecyclerView using binding
        binding.profileRecyclerView.apply {
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(this@ProfileListActivity)
        }

        // 3. Initial Profile Observation
        // Start by observing all profiles (empty search query)
        setupProfileObservation("")


        // 4. Set up Search Functionality (TextWatcher)
        binding.searchProfile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When text changes, update the profile observation based on the query
                val query = s.toString()
                setupProfileObservation(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // 5. Setup Action Listeners

        // Item Click (Detail)
        profileAdapter.setOnItemClickListener { userProfile ->
            val intent = Intent(this@ProfileListActivity, ProfileDetailActivity::class.java)
            intent.putExtra("USER_PROFILE", userProfile)
            startActivity(intent)
        }

        // Delete Click
        profileAdapter.setOnDeleteClickListener { userProfile ->
            profileViewModel.deleteUserProfile(userProfile)
        }

        // Update Click
        profileAdapter.setOnUpdateClickListener { userProfile ->
            val intent = Intent(this@ProfileListActivity, UpdateProfileActivity::class.java)
            intent.putExtra("USER_PROFILE", userProfile)
            startActivity(intent)
        }

        // Add Button Click (using binding)
        // Since the observer is already running, adding a profile will automatically update the count.
        binding.addProfileBtn.setOnClickListener {
            val intent = Intent(this@ProfileListActivity, AddProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Sets up or switches the LiveData observation based on the search query.
     * This method ensures the profile count TextView updates whenever the data changes
     * (e.g., when a profile is added, deleted, or the search filter is applied).
     */
    private fun setupProfileObservation(query: String) {
        // 1. Clear any previous observation to prevent multiple observers running
        currentProfilesLiveData?.removeObservers(this)

        // 2. Get the new LiveData based on the query
        // IMPORTANT: You need to ensure profileViewModel.searchUserProfiles() is implemented
        // to handle both filtering (when query is not empty) and returning all data (when query is empty).
        val newProfilesLiveData = profileViewModel.searchUserProfiles(query)
        currentProfilesLiveData = newProfilesLiveData // Update the tracker

        // 3. Observe the new LiveData
        newProfilesLiveData.observe(this, Observer { profiles ->
            profileAdapter.submitList(profiles)

            // 4. Update the profile count TextView
            // The count will automatically reflect the size of the current list (filtered or full).
            binding.profileCount.text = "Profile Count: ${profiles.size}"
        })
    }
}