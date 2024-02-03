package com.example.practice_solid

import com.example.practice_solid.model.Device
import com.example.practice_solid.model.Distributor
import com.example.practice_solid.model.User
import com.example.practice_solid.model.AccessType
import com.example.practice_solid.utils.DeviceOwnershipRegistry
import com.example.practice_solid.utils.DistributorPermissionManager
import com.example.practice_solid.model.Permission
import com.example.practice_solid.utils.PermissionManager
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ExampleUnitTest {
    private lateinit var devices: MutableList<Device>
    private lateinit var user: User
    private lateinit var distributor1: Distributor
    private lateinit var distributor2: Distributor

    @Before
    fun setup() {
        // Initialize your devices, users, and distributors here
        devices = mutableListOf(
            Device("device1", "Device 1", "distributor1"),
            Device("device2", "Device 2", "distributor1"),
            Device("device3", "Device 3", "distributor2"),
            Device("device4", "Device 4", "distributor2"),
            Device("device5", "Device 5", ""),
        )

        val sharedPermissions = mutableListOf<Permission>()

        val permissionManagerUser = PermissionManager(sharedPermissions)
        val permissionManagerDistributor1 = DistributorPermissionManager(sharedPermissions)
        val permissionManagerDistributor2 = DistributorPermissionManager(sharedPermissions)

        user = User("user1", "User One", permissionManagerUser)
        distributor1 = Distributor("distributor1", "Distributor One", mutableListOf("device1", "device2"), permissionManagerDistributor1)
        distributor2 = Distributor("distributor2", "Distributor Two", mutableListOf("device3", "device4"), permissionManagerDistributor2)

        devices.forEach { device ->
            DeviceOwnershipRegistry.registerDeviceOwnership(device.id, device.owner)
        }
    }
    @Test
    fun `Test list of devices a user can access`() {
        // Setup: Assign read-only permission to user for device1
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY)

        val accessibleDevices = user.getUserPermissions().map { it.deviceId }

        assertTrue("User should have access to device1", accessibleDevices.contains("device1"))
        assertFalse("User should not have access to device2", accessibleDevices.contains("device2"))
    }

    @Test
    fun `Test list of users who have access to a device`() {
        // Setup: Assign permissions
        distributor1.assignGlobalPermission("user1", AccessType.READ_ONLY, overrideSpecific = false)
        distributor1.assignGlobalPermission("distributor2", AccessType.FULL_ACCESS, true)

        val devicePermissions = distributor1.getDevicePermissions("device1")
        assertTrue("User1 should have READ_ONLY access to device1", devicePermissions.any { it.userId == "user1" && it.accessType == AccessType.READ_ONLY })
        assertTrue("Distributor 2 should have FULL_ACCESS to device1",devicePermissions.any { it.userId == "distributor2" && it.accessType == AccessType.FULL_ACCESS })
    }

    @Test
    fun `Assign and remove read-only permission`() {
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY)
        var permissions = user.getUserPermissions()
        assertTrue("User should have READ_ONLY permission", permissions.any { it.accessType == AccessType.READ_ONLY })

        distributor1.removePermission("user1", "device1")
        permissions = user.getUserPermissions()
        assertTrue("Permissions should be empty after removal", permissions.isEmpty())
    }

    @Test
    fun `Assign and remove full access permission`() {
        distributor1.assignSpecificPermission("user1", "device1", AccessType.FULL_ACCESS)
        var permissions = user.getUserPermissions()
        assertTrue("User should have FULL_ACCESS permission", permissions.any { it.accessType == AccessType.FULL_ACCESS })

        distributor1.removePermission("user1", "device1")
        permissions = user.getUserPermissions()
        assertTrue("Permissions should be empty after removal", permissions.isEmpty())
    }

    @Test
    fun `Assign global READ_ONLY permission to another user for all devices of a Distributor`() {
        distributor1.assignGlobalPermission("user2", AccessType.READ_ONLY, overrideSpecific = false)
        val permissions = distributor1.getUserPermissions("user2")
        assertTrue("User2 should have READ_ONLY access to all distributor1's devices", permissions.all { it.accessType == AccessType.READ_ONLY })
    }

    @Test
    fun `Assign global FULL_ACCESS permission for Distributor devices but configure a specific device to READ_ONLY`() {
        distributor1.assignGlobalPermission("user1", AccessType.FULL_ACCESS, overrideSpecific = true)
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY) // Set device1 to READ_ONLY

        val permissions = distributor1.getUserPermissions("user1")
        assertTrue("User1 should have READ_ONLY permission for device1", permissions.any { it.deviceId == "device1" && it.accessType == AccessType.READ_ONLY })
        assertTrue("User1 should have FULL_ACCESS permission for other devices", permissions.any { it.deviceId != "device1" && it.accessType == AccessType.FULL_ACCESS })
    }
}