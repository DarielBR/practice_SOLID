package com.example.practice_solid.utils

import com.example.practice_solid.model.AccessType
import com.example.practice_solid.model.Permission

interface PermissionManager{
    fun getPermissionsByUser(userId: String): List<Permission>
}

open class UserPermissionManager(
    private val permissions: MutableList<Permission> = mutableListOf()
): PermissionManager {
    /**
     * Returns a list of permission for a specific user.
     * @param userId to filter for.
     * @return
     */
    override fun getPermissionsByUser(userId: String): List<Permission> {
        return permissions.filter { it.userId == userId }
    }
}

class DistributorPermissionManager(
    private val permissions: MutableList<Permission> = mutableListOf()
): PermissionManager {

    /**
     * Sets a permission type for a given user in a number of devices (Global permission assignment).
     * @param userId whose permission are going to be granted.
     * @param devices id of devices that will be accessed.
     * @param accessType the type og permission.
     * @param overrideSpecific whether or not override any specific permissions already given.
     */
    fun assignGlobalPermission(
        userId: String,
        devices: List<String>,
        accessType: AccessType,
        overrideSpecific: Boolean
    ) {
        devices.forEach { deviceId ->
            val index = permissions.indexOfFirst { it.deviceId == deviceId && it.userId == userId }
            if(index != -1){
                if (!permissions[index].isSpecific || overrideSpecific)
                    permissions[index] = permissions[index].copy(accessType = accessType, isSpecific = false)
            }else{
                permissions.add(
                    Permission(
                        userId = userId,
                        deviceId = deviceId,
                        accessType = accessType,
                        isSpecific = false
                    )
                )
            }
        }
    }

    /**
     * Set a specific permission type over a device for a given user. Applying this function will
     * brand the permission as "Specific", thus, will be not modified in a global permission assignment operation.
     * @param userId whose permission is going to be modified.
     * @param deviceId the specific recipient of the permission.
     * @param accessType the type of permission.
     */
    fun assignSpecificPermission(
        userId: String,
        deviceId: String,
        accessType: AccessType
    ) {
        val index = permissions.indexOfFirst { it.deviceId == deviceId && it.userId == userId }
        if (index != -1){
            permissions[index] = permissions[index].copy(accessType = accessType, isSpecific = true)
        }else{
            permissions.add(
                Permission(
                    userId = userId,
                    deviceId = deviceId,
                    accessType = accessType,
                    isSpecific = true
                )
            )
        }
    }

    /**
     * Deletes a specific permission over a device for a give user.
     * @param userId whose permission is going to be eliminated.
     * @param deviceId the specific device, recipient of the the permission.
     */
    fun removePermission(userId: String, deviceId: String) {
        val index = permissions.indexOfFirst { it.deviceId == deviceId && it.userId == userId }
        if (index != -1) permissions.removeAt(index)
    }

    /**
     * Returns a list of permission over a specific device.
     * @param deviceId to filter for.
     * @return
     */
    fun getPermissionsByDevice(deviceId: String): List<Permission> {
        return permissions.filter { it.deviceId == deviceId }
    }

    /**
     * Returns a list of permission for a specific user.
     * @param userId to filter for.
     * @return
     */
    override fun getPermissionsByUser(userId: String): List<Permission> {
        return permissions.filter { it.userId == userId }
    }
}