package com.example.practice_solid.model

import com.example.practice_solid.utils.Permission
import com.example.practice_solid.utils.PermissionManager

open class User (
    open val id: String,
    open val name: String,
    private val permissionManager: PermissionManager,
){
    fun getUserPermissions(): List<Permission>{
        return permissionManager.getPermissionsByUser(id)
    }
}