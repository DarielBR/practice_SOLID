package com.example.practice_solid.utils

object DeviceOwnershipRegistry {
    private val deviceOwnerMap = mutableMapOf<String, String>()

    fun registerDeviceOwnership(deviceId: String, distributorId: String) {
        deviceOwnerMap[deviceId] = distributorId
    }

    fun getOwnerOfDevice(deviceId: String): String? {
        return deviceOwnerMap[deviceId]
    }

    fun releaseDeviceOwnership(deviceId: String) {
        deviceOwnerMap.remove(deviceId)
    }

    fun getDevicesOwnedByDistributor(distributorId: String): List<String> {
        return deviceOwnerMap.filterValues { it == distributorId }.keys.toList()
    }
}