package com.sarrawi.mysocialnetwork.model.ProfileModel

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val profile: ProfileResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
