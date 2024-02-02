package com.example.practice_solid.model

import com.example.practice_solid.utils.AccessType
import com.example.practice_solid.utils.DistributorPermissionManager
import com.example.practice_solid.utils.Permission

class Distributor(
    override val id: String,
    override val name: String,
    private val devices: MutableList<String>,
    private val permissionManager: DistributorPermissionManager,
): User(id, name, permissionManager) {

    fun assignGlobalPermission(
        userId: String,
        accessType: AccessType,
        overrideSpecific: Boolean = false,
    ){
        permissionManager.assignGlobalPermission(userId, devices, accessType, overrideSpecific)
    }

    fun assignSpecificPermission(
        userId: String,
        deviceId: String,
        accessType: AccessType,
    ){
        permissionManager.assignSpecificPermission(userId, deviceId, accessType)
    }

    fun removePermission(
        userId: String,
        deviceId: String
    ){
        permissionManager.removePermission(userId, deviceId)
    }

    fun getDevicePermissions(deviceId: String): List<Permission>{
        return permissionManager.getPermissionsByDevice(deviceId)
    }

    fun getUserPermissions(userId: String): List<Permission>{
        return permissionManager.getPermissionsByUser(userId)
    }

    fun addDevice(deviceId: String){
        devices.add(deviceId)
        permissionManager.assignSpecificPermission(id, deviceId, AccessType.FULL_ACCESS)
    }

    fun deleteDevice(deviceId: String){
        devices.remove(deviceId)
        permissionManager.removePermission(id, deviceId)
    }

    init {
        /*TODO*/
    }
}