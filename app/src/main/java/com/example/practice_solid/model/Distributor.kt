package com.example.practice_solid.model

import com.example.practice_solid.utils.DistributorDeviceManager
import com.example.practice_solid.utils.DistributorPermissionManager

class Distributor(
    override val id: String,
    override val name: String,
    private val deviceManager: DistributorDeviceManager,
    private val permissionManager: DistributorPermissionManager,
): User(id, name, permissionManager) {
    private fun getDevicesIds() = deviceManager.getOwnedDeviceList(id).map { it.id }
    fun assignGlobalPermission(
        userId: String,
        accessType: AccessType,
        overrideSpecific: Boolean = false,
    ){
        permissionManager.assignGlobalPermission(userId, getDevicesIds(), accessType, overrideSpecific)
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
}