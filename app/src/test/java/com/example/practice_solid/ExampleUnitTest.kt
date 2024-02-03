package com.example.practice_solid

import com.example.practice_solid.model.Device
import com.example.practice_solid.model.Distributor
import com.example.practice_solid.model.User
import com.example.practice_solid.model.AccessType
import com.example.practice_solid.utils.DistributorPermissionManager
import com.example.practice_solid.model.Permission
import com.example.practice_solid.utils.DistributorDeviceManager
import com.example.practice_solid.utils.UserPermissionManager
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class ExampleUnitTest {
    //private lateinit var devices: MutableList<Device>
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var distributor1: Distributor
    private lateinit var distributor2: Distributor

    @Before
    fun setup() {
        // Initialize your devices, users, and distributors here
        val sharedDeviceList = mutableListOf(
            Device("device1", "Device One", "distributor1"),
            Device("device2", "Device Two", "distributor1"),
            Device("device3", "Device Three", "distributor2"),
            Device("device4", "Device Four", "distributor2"),
            Device("device5", "Device Five", ""),
        )

        val deviceManager = DistributorDeviceManager(sharedDeviceList)

        val sharedPermissions = mutableListOf<Permission>()

        val permissionManagerUser = UserPermissionManager(sharedPermissions)
        val permissionManagerDistributor = DistributorPermissionManager(sharedPermissions)

        user1 = User("user1", "User One", permissionManagerUser)
        user2 = User("user2", "User Two", permissionManagerUser)
        distributor1 = Distributor("distributor1", "Distributor One", deviceManager, permissionManagerDistributor)
        distributor2 = Distributor("distributor2", "Distributor Two", deviceManager, permissionManagerDistributor)


    }
    @Test
    fun `Test-1 list of devices a user can access`() {
        // Setup: Assign read-only permission to User One for Device Two
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY)

        var accessibleDevices = user1.getUserPermissions().map { it.deviceId }

        assertTrue("User One should have access to Device One", accessibleDevices.contains("device1"))
        assertFalse("User One should not have access to Device Two", accessibleDevices.contains("device2"))

        // Setup: Assign read-only permission to User One for Device Two
        distributor1.assignGlobalPermission("user1", AccessType.READ_ONLY, false)

        accessibleDevices = user1.getUserPermissions().map { it.deviceId }

        assertTrue("User One should have access to Device One", accessibleDevices.contains("device1"))
        assertTrue("User One should have access to Device Two", accessibleDevices.contains("device2"))
        assertFalse("User One should not have access to Device Three", accessibleDevices.contains("device3"))
    }

    @Test
    fun `Test-2 list of users who have access to a device`() {
        // Setup: Assign permissions
        distributor1.assignGlobalPermission("user1", AccessType.READ_ONLY, overrideSpecific = false)
        distributor1.assignSpecificPermission("distributor2", "device1", AccessType.FULL_ACCESS)

        val devicePermissions = distributor1.getDevicePermissions("device1")
        assertTrue("User1 should have READ_ONLY access to Device One", devicePermissions.any { it.userId == "user1" && it.accessType == AccessType.READ_ONLY })
        assertTrue("Distributor Two should have FULL_ACCESS to Device One",devicePermissions.any { it.userId == "distributor2" && it.accessType == AccessType.FULL_ACCESS })
        assertFalse("User1 should not have FULL_ACCESS over Device One", devicePermissions.any { it.userId == "user1" && it.accessType == AccessType.FULL_ACCESS })
        assertFalse("User Two should not have any access level over Device One", devicePermissions.any { it.userId == "user2" })
    }

    @Test
    fun `Test-3 Assign and remove READ_ONLY permission`() {
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY)
        var permissions = user1.getUserPermissions()
        assertTrue("User should have READ_ONLY permission", permissions.any { it.accessType == AccessType.READ_ONLY })

        distributor1.removePermission("user1", "device1")
        permissions = user1.getUserPermissions()
        assertTrue("Permissions should be empty after removal", permissions.isEmpty())
    }

    @Test
    fun `Test-4 Assign and remove FULL_ACCESS permission`() {
        distributor1.assignSpecificPermission("user1", "device1", AccessType.FULL_ACCESS)
        var permissions = user1.getUserPermissions()
        assertTrue("User should have FULL_ACCESS permission", permissions.any { it.accessType == AccessType.FULL_ACCESS })

        distributor1.removePermission("user1", "device1")
        permissions = user1.getUserPermissions()
        assertTrue("Permissions should be empty after removal", permissions.isEmpty())
    }

    @Test
    fun `Test-5 Assign global READ_ONLY permission to another user for all devices of a Distributor`() {
        distributor1.assignGlobalPermission("user2", AccessType.READ_ONLY, overrideSpecific = true)
        val permissions = user2.getUserPermissions()
        assertTrue("User Two should have READ_ONLY access to all Distributor One's devices", permissions.all { it.accessType == AccessType.READ_ONLY })
    }

    @Test
    fun `Test -6 Assign global FULL_ACCESS permission for Distributor devices but configure a specific device to READ_ONLY`() {
        //Set FULL_ACCESS to User One to all Distributor One's devices
        distributor1.assignGlobalPermission("user1", AccessType.FULL_ACCESS, overrideSpecific = true)

        var user1Permissions = user1.getUserPermissions()

        assertTrue("User1 should have FULL_ACCESS permission for Device One.", user1Permissions.any { it.deviceId != "device1" && it.accessType == AccessType.FULL_ACCESS })
        assertTrue("User1 should have FULL_ACCESS permission for Device Two.", user1Permissions.any { it.deviceId != "device2" && it.accessType == AccessType.FULL_ACCESS })

        //Downgrade to READ_ONLY to User One for Device 1
        distributor1.assignSpecificPermission("user1", "device1", AccessType.READ_ONLY)

        user1Permissions = user1.getUserPermissions()

        assertTrue("User1 should have READ_ONLY permission for Device One", user1Permissions.any { it.deviceId == "device1" && it.accessType == AccessType.READ_ONLY })
        assertTrue("User1 should have FULL_ACCESS permission for Device Two", user1Permissions.any { it.deviceId == "device2" && it.accessType == AccessType.FULL_ACCESS })
        assertFalse("User1 should not have FULL_ACCESS permission for Device One", user1Permissions.any { it.deviceId == "device1" && it.accessType == AccessType.FULL_ACCESS })
    }
}