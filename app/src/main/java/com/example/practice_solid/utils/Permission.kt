package com.example.practice_solid.utils

data class Permission (
    val userId: String,
    val deviceId: String,
    val accessType: AccessType,
    val isSpecific: Boolean,
)

enum class AccessType {
    READ_ONLY,
    FULL_ACCESS,
}