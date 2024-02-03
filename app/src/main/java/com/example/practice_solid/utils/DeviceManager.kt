package com.example.practice_solid.utils

import com.example.practice_solid.model.Device

interface DeviceManager{
    fun setOwnership(deviceId: String, distributorId: String): Device?
}

class DistributorDeviceManager(
    private val devices: MutableList<Device> = mutableListOf()
): DeviceManager {
    /**
     * Returns a list with devices owned by a given user(distributor).
     * @param distributorId
     * @return a list with user's devices, empty list if there are none devices owned by user.
     */
    fun getOwnedDeviceList(distributorId: String): List<Device> {
        return devices.filter { it.owner == distributorId }
    }
    /**
     * Sets Distributor ownership over a device
     *
     * @param deviceId
     * @param distributorId
     * @return the modified device or null if the device does not exist.
     */
    override fun setOwnership(deviceId: String, distributorId: String): Device?{
        val index = devices.indexOfFirst { it.id == deviceId }
        return if (index != -1) {
            devices[index] = devices[index].copy(owner = distributorId)
            devices[index]
        }else null
    }
}
class AdministratorDeviceManager(
    private val devices: MutableList<Device> = mutableListOf()
):DeviceManager {
    /**
     * Creates a new device into.
     * @param deviceId unique device identifier.
     * @param name
     * @param owner Distributor holding ownership over device.
     */
    fun createNewDevice(deviceId: String, name: String, owner: String? = null){
        devices.add(Device(
                id = deviceId,
                name = name,
                owner = owner ?: ""
            )
        )
    }

    /**
     * Sets Distributor ownership over a device
     *
     * @param deviceId
     * @param distributorId
     * @return the modified device or null if the device does not exist.
     */
    override fun setOwnership(deviceId: String, distributorId: String): Device?{
        val index = devices.indexOfFirst { it.id == deviceId }
        return if (index != -1) {
            devices[index] = devices[index].copy(owner = distributorId)
            devices[index]
        }else null
    }

    /**
     * Removes a device.
     * @param deviceId
     */
    fun deleteDevice(deviceId: String){
        devices.removeIf { it.id == deviceId }
    }
}