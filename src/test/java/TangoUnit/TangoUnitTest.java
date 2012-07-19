package TangoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DbDatum;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.TangoApi.DeviceInfo;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.soleil.tango.util.WaitStateUtilities;
import fr.soleil.tangounit.client.TangoUnitClient;
import fr.soleil.tangounit.client.TangoUnitFactory;
import fr.soleil.tangounit.client.TangoUnitFactory.MODE;
import fr.soleil.tangounit.device.Device;

public class TangoUnitTest {

	static TangoUnitClient tangoUnit;
	static DeviceProxy deviceTangoUnit = null;

	@BeforeClass
	public static void setUpBeforeClass() throws DevFailed, TimeoutException {

		try {
			ApiUtil.get_db_obj().set_timeout_millis(10000);
			// TangoUnit
			tangoUnit = TangoUnitFactory.instance().createTangoUnitClient(
					MODE.remote);
			// Server to test defined by the program name
			tangoUnit.setTestServer("TangoUnit");
			// Device to test only defined by Class
			Device d = tangoUnit.addTestDevice("TangoUnit"); // TODO replace
			// Device

			// Create the environment
			tangoUnit.create().start();

			// Start device of this class
			tangoUnit.executeTestDevice();

			// Build proxy after execution !!!!
			deviceTangoUnit = d.getProxy();
		} catch (DevFailed e) {
			Except.print_exception(e);
			throw e;
		} catch (TimeoutException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		// This session will delete information about
		// Test Devices
		TangoUnitFactory.instance().releaseTangoUnitClient(tangoUnit);
		// Client have the responsability to stop the server to test
		// Here the end of test close the jvm and so Server itself

		// Stop Executable
	}

	@Test
	public void testInit_device() throws DevFailed, TimeoutException {
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		DevState state = deviceTangoUnit.state();

		assertTrue(((state == DevState.INIT) || (state == DevState.STANDBY)));

		// Test Initialize state

		// Here modify the current state of device
		// 1- Add device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		deviceTangoUnit.command_inout("AddDevice", data); // Supposed AddDevice
		// work !!

		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		String[] expected = new String[] {};
		DeviceAttribute a = deviceTangoUnit.read_attribute("devices");
		String[] actuals = a.extractStringArray();
		assertEquals(expected, actuals); // Check if device is empty
	}

	@Test
	public void testRead_attrAttribute() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		// Empty
		String[] expected = new String[] {};
		DeviceAttribute a = deviceTangoUnit.read_attribute("devices");
		String[] actuals = a.extractStringArray();
		assertEquals(expected, actuals);

		// Add a device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		deviceTangoUnit.command_inout("AddDevice", data); // Supposed AddDevice
		// work !!

		// 1 device
		a = deviceTangoUnit.read_attribute("devices");
		actuals = a.extractStringArray();
		assertTrue(actuals.length == 5);
	}

	@Test
	public void testAdd_device() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		DeviceAttribute a = null;
		String[] actuals = null;

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		deviceTangoUnit.command_inout("AddDevice", data);

		// 1 device
		a = deviceTangoUnit.read_attribute("devices");
		actuals = a.extractStringArray();
		// expected [Name Of Device][Name of Class][Name of DeviceServer][Name
		// of Starter][Platform of Starter]
		assertTrue(actuals.length == 5);
		assertEquals("We know only about the class", "TangoTest", actuals[1]);

		// Add another device from same class
		data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		deviceTangoUnit.command_inout("AddDevice", data);

		// 2 device
		a = deviceTangoUnit.read_attribute("devices");
		actuals = a.extractStringArray();
		// expected [Name Of Device][Name of Class][Name of DeviceServer][Name
		// of Starter][Platform of Starter]
		assertTrue(actuals.length == 5 * 2);
		assertEquals("We know only about the class", "TangoTest",
				actuals[5 + 1]);

	}

	@Test
	public void testCreate() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		data = deviceTangoUnit.command_inout("AddDevice", data);
		String actual = data.extractString();
		assertNotNull(actual);

		// We check that the device is not already registered in database
		try {
			ApiUtil.get_db_obj().get_device_info(actual);
			fail("This device should not exist");
		} catch (DevFailed e) {
		}

		// Create this device
		deviceTangoUnit.command_inout("Create");

		// Don't check if it's really added
		// We already do it in testAdd_device()
		// We will check if TangoUnit device really create the device in
		// database
		DeviceInfo info = ApiUtil.get_db_obj().get_device_info(actual);
		assertTrue(actual.equalsIgnoreCase(info.name));

	}

	@Test
	public void testStart() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		data = deviceTangoUnit.command_inout("AddDevice", data);
		String device = data.extractString();

		// Create this device
		deviceTangoUnit.command_inout("Create");
		// Start all device
		deviceTangoUnit.command_inout("Start");

		this.failIfWrongState(deviceTangoUnit, DevState.ON);

		// Don't check if it's really added
		// Don't check if it's really registed in database
		// just check if it is alive
		DeviceInfo info = ApiUtil.get_db_obj().get_device_info(device);
		assertTrue(info.exported);

		DeviceProxy tangotest = new DeviceProxy(device);
		tangotest.ping();
	}

	/**
	 * @throws DevFailed
	 * @throws TimeoutException
	 */
	private void failIfWrongState(DeviceProxy deviceProxy, DevState expected)
			throws DevFailed, TimeoutException {
		WaitStateUtilities.waitWhileState(deviceProxy, DevState.MOVING, 30000);
		assertEquals(expected, deviceProxy.state());
	}

	@Test
	public void testStop() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		data = deviceTangoUnit.command_inout("AddDevice", data);
		String device = data.extractString();

		// Create this device
		deviceTangoUnit.command_inout("Create");
		// Start all device
		deviceTangoUnit.command_inout("Start");
		this.failIfWrongState(deviceTangoUnit, DevState.ON);

		// Stop all device
		deviceTangoUnit.command_inout("Stop");
		this.failIfWrongState(deviceTangoUnit, DevState.OFF);

		// Don't check if it's really added
		// Don't check if it's really registed in database
		// Don't check if it's really started
		// just check if it isn't alive
		DeviceInfo info = ApiUtil.get_db_obj().get_device_info(device);
		assertTrue(!info.exported);

		try {
			DeviceProxy tangotest = new DeviceProxy(device);
			tangotest.ping();
			fail();
		} catch (DevFailed e) {
			assertTrue("This device isn't alive Yep!!!", true);
		}
	}

	@Test
	public void testDelete() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		data = deviceTangoUnit.command_inout("AddDevice", data);
		String device = data.extractString();

		// Create this device
		deviceTangoUnit.command_inout("Create");
		// Start all device
		deviceTangoUnit.command_inout("Start");
		this.failIfWrongState(deviceTangoUnit, DevState.ON);

		// Stop all device
		deviceTangoUnit.command_inout("Stop");
		this.failIfWrongState(deviceTangoUnit, DevState.OFF);
		// Delete all device
		deviceTangoUnit.command_inout("Delete");

		// Don't check if it's really added
		// Don't check if it's really registed in database
		// Don't check if it's really started
		// Don't check if it's really stopped
		// just check if it isn't registered in database

		try {
			ApiUtil.get_db_obj().get_device_info(device);
			fail();
		} catch (DevFailed e) {
			assertTrue("This device isn't registered Yep!!!", true);
		}
	}

	@Test
	public void testState() throws DevFailed, InterruptedException,
			TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		assertEquals(DevState.STANDBY, deviceTangoUnit.state());

		//
		// WO Device
		// Work only in synchronous mode
		// 
		// TODO Asynchronous
		deviceTangoUnit.command_inout("Create");
		assertEquals(DevState.OFF, deviceTangoUnit.state());

		deviceTangoUnit.command_inout("Start");
		this.failIfWrongState(deviceTangoUnit, DevState.ON);

		deviceTangoUnit.command_inout("Stop");
		this.failIfWrongState(deviceTangoUnit, DevState.OFF);

		deviceTangoUnit.command_inout("Delete");
		assertEquals(DevState.STANDBY, deviceTangoUnit.state());

		// Add a TangoTest device
		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		deviceTangoUnit.command_inout("AddDevice", data);

		// Create this device
		deviceTangoUnit.command_inout("Create");
		assertEquals(DevState.OFF, deviceTangoUnit.state());
		// Start all device
		deviceTangoUnit.command_inout("Start");
		this.failIfWrongState(deviceTangoUnit, DevState.ON);
		// Stop all device
		deviceTangoUnit.command_inout("Stop");
		this.failIfWrongState(deviceTangoUnit, DevState.OFF);
		// Delete all device
		deviceTangoUnit.command_inout("Delete");
		assertEquals(DevState.STANDBY, deviceTangoUnit.state());
	}

	@Test
	public void testAdd_device_properties() throws DevFailed, TimeoutException {
		// do init supposed be in the same state for each unit test
		deviceTangoUnit.command_inout("Init");
		WaitStateUtilities.waitForState(deviceTangoUnit, DevState.STANDBY, 30000);

		DeviceData data = new DeviceData();
		data.insert(new String[] { "TangoTest" });
		data = deviceTangoUnit.command_inout("AddDevice", data);
		String firstDevice = data.extractString();

		// *** TEST 1 ***
		String propertyName = "UnitTest";
		String expected = "TestOK";
		// Create a new property
		data.insert(new String[] { firstDevice, propertyName, expected });
		deviceTangoUnit.command_inout("AddDeviceProperties", data);
		deviceTangoUnit.command_inout("Create");
		DbDatum datum = ApiUtil.get_db_obj().get_device_property(firstDevice,
				propertyName);
		assertEquals(expected, datum.extractString());

		// Clean Database
		deviceTangoUnit.command_inout("Delete");

		// *** TEST 2 ***
		// check property with array value
		propertyName = "UnitTestMultipleValue";
		String[] expected2 = new String[] { "TestOK", "With multiple value" };
		// Create a new property
		data.insert(new String[] { firstDevice, propertyName, expected2[0],
				expected2[1] });
		deviceTangoUnit.command_inout("AddDeviceProperties", data);
		deviceTangoUnit.command_inout("Create");
		datum = ApiUtil.get_db_obj().get_device_property(firstDevice,
				propertyName);
		assertEquals(expected2, datum.extractStringArray());

	}

}
