package com.smokinmonkey.nada.util

// data class for user info
data class User(
    // user base info
    val uid: String? = "",
    val email: String? = "",
    val username: String? = "",
    val imageUrl: String? = "",
    // use profile activity to have more detail info
    // add match preference
    val gender: String? = "",
    val preferredGender: String? = "",
    val age: String? = "",
    val birthday: String? = ""
    // matches
//    val likes: ArrayList<String>? = arrayListOf(),
//    val unlikes: ArrayList<String>? = arrayListOf()
)
// data class for posts
data class Post(
    val postId: String? = "",
    val userId: String? = "",
    val userIds: ArrayList<String>? = arrayListOf(),
    val userImage: String? = "",
    val username: String? = "",
    val title: String? = "",
    val text: String? = "",
    val imageUrl: String? = "",
    val timestamp: Long? = 0,
    val likes: ArrayList<String>? = arrayListOf(),
    val comments: ArrayList<String>? = arrayListOf()
)
// data class for chat
data class Chat(
    val chatId: String? = "",
    val userId: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val otherImageUrl: String? = "",
    val otherUserId: String? = ""
)
// data class for message
data class Message(
    val sentById: String? = "",
    val sentByImageUrl: String? = "",
    val message: String? = "",
    val time: String? = ""
)