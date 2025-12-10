package com.example.roomdatabse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabse.R

class ProfileAdapter : ListAdapter<UserProfile, ProfileAdapter.ProfileViewHolder>(DiffCallback()) {

    // DEFINE DRAWABLE ICONS ARRAY
    private val PROFILE_PICS = intArrayOf(
        R.drawable.person,
        R.drawable.person2,
        R.drawable.smiley,
        R.drawable.count
    )

    // listener for icon change
    private var onIconChangeClickListener: ((UserProfile) -> Unit)? = null
    private var onItemClickListener: ((UserProfile) -> Unit)? = null
    private var onDeleteClickListener: ((UserProfile) -> Unit)? = null
    private var onUpdateClickListener: ((UserProfile) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_item_layout, parent, false)
        return ProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, this) // <-- FIX: Passing 'this' (the adapter instance)
    }

    // Setter for the new listener
    fun setOnIconChangeClickListener(listener: (UserProfile) -> Unit) {
        onIconChangeClickListener = listener
    }

    fun setOnItemClickListener(listener: (UserProfile) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (UserProfile) -> Unit) {
        onDeleteClickListener = listener
    }

    fun setOnUpdateClickListener(listener: (UserProfile) -> Unit) {
        onUpdateClickListener = listener
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // ADD ImageView FIELD
        private val profilePic: ImageView = itemView.findViewById(R.id.profilePic)
        private val profileName: TextView = itemView.findViewById(R.id.userNameTxt)
        private val profileEmail: TextView = itemView.findViewById(R.id.userEmailTxt)
        private val profileDOB: TextView = itemView.findViewById(R.id.userDOBTxt)
        private val profileDistrict: TextView = itemView.findViewById(R.id.userDistritTxt)
        private val profileMobile: TextView = itemView.findViewById(R.id.userMobileTxt)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteBtn)
        private val updateButton: ImageButton = itemView.findViewById(R.id.editBtn)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val profile = getItem(position)
                    onItemClickListener?.invoke(profile)
                }
            }

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val profile = getItem(position)
                    onDeleteClickListener?.invoke(profile)
                }
            }

            updateButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val profile = getItem(position)
                    onUpdateClickListener?.invoke(profile)
                }
            }

            // IMPLEMENT TAP-TO-CYCLE LOGIC
            profilePic.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentProfile = getItem(position)

                    // Calculate the new icon index, cycling back to 0 if max is reached
                    // Note: R.drawable.person/person2/smiley/count must exist!
                    val newIndex = (currentProfile.profileIconIndex + 1) % PROFILE_PICS.size

                    // Create a new UserProfile object with the updated index
                    val updatedProfile = currentProfile.copy(profileIconIndex = newIndex)

                    // Update the UI immediately
                    profilePic.setImageResource(PROFILE_PICS[newIndex])

                    // Notify the Activity/ViewModel to save this change to the database
                    onIconChangeClickListener?.invoke(updatedProfile)
                }
            }
        }

        fun bind(userProfile: UserProfile, adapter: ProfileAdapter) { // <-- Updated signature to take adapter
            // SET THE INITIAL PROFILE ICON
            val iconIndex = userProfile.profileIconIndex.coerceIn(0, adapter.PROFILE_PICS.size - 1)
            profilePic.setImageResource(adapter.PROFILE_PICS[iconIndex])

            profileName.text = userProfile.name
            profileEmail.text = userProfile.email
            profileDOB.text = userProfile.dob
            profileDistrict.text = userProfile.district
            profileMobile.text = userProfile.mobile
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem == newItem
        }
    }
}