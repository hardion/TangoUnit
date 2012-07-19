/**
 * 
 */
package fr.soleil.tangounit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.soleil.tangounit.Session.Configuration;
import fr.soleil.tangounit.device.Device;
import fr.soleil.tangounit.device.Factories;
import fr.soleil.tangounit.device.Server;
import fr.soleil.tangounit.device.Starter.Platform;

/**
 * @author HARDION
 * 
 */
public class SessionTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// XXX too dependent of Tango environment
		//Factories.addStarter("tango/admin/pci108", Platform.win32);

		ApiUtil.get_db_obj().set_timeout_millis(10000);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfter() throws Exception {

	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#Session()}.
	 */
	@Test
	public void testSession() throws DevFailed {
		assertNotNull(new Session());
	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#Session()}.
	 */
	@Test
	public void testDelete() throws DevFailed {
		Session session = new Session();

		// Black Box
		Device d = session.addDevice("TangoTest");
		session.create();
		// Check if device is registered
		String[] ad = ApiUtil.get_db_obj().getDevices(d.getName());
		assertEquals(new String[] { d.getName() }, ad);

		session.delete();
		ad = ApiUtil.get_db_obj().getDevices(d.getName());
		assertEquals(new String[] {}, ad);

		Server serverOfD = null;
		for (Configuration conf : session.configurations) {
			for (Server s : conf) {
				for (Device dev : s.getChildren()) {
					if (dev.equals(d)) {
						serverOfD = s;
						break;
					}
				}
				if (serverOfD != null) {
					break;
				}
			}
		}

		String[] actual = ApiUtil.get_db_obj().get_server_list(
				serverOfD.getName());
		assertEquals(new String[] {}, actual);

		session.init();

	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#addDevice()}.
	 */
	@Test
	public void testCreateOneAnonymousDevice() throws DevFailed {
		Session session = new Session();

		try {
			Device tangotest = session.addDevice("TangoTest");
			System.out.println(tangotest.getName());

			assertNotNull(tangotest);

			session.create().start();

			DeviceAttribute attr = tangotest.getProxy().read_attribute(
					"double_scalar");
			attr.extractDouble();
			assertTrue("Device was created and it works", true);
		} catch (DevFailed e) {
			assertTrue(printStackTrace(e).toString(), false);
		} finally {
			// It should be automatic
			session.init();
		}
	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#addDevice()}.
	 * 
	 * @throws DevFailed
	 */
	@Test
	public void testCreateOneDevice() throws DevFailed {
		Session session = new Session();

		try {

			Device tangotest = session.addQualifiedDevice("TangoTest",
					"test/tangounit/userdefined");

			session.create().start();

			DeviceAttribute attr = tangotest.getProxy().read_attribute(
					"double_scalar");
			attr.extractDouble();
			assertTrue("Device was created and it works", true);

		} catch (DevFailed e) {
			assertTrue(printStackTrace(e).toString(), false);
		} finally {
			// It should be automatic
			session.init();

		}
	}

	private static StringBuffer printStackTrace(DevFailed e) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < e.errors.length; i++) {
			buf.append("Level " + i);
			buf.append(e.errors[i].desc);
			buf.append("\n");
			buf.append(e.errors[i].origin);
			buf.append("\n");
			buf.append(e.errors[i].reason);
			buf.append("\n");
		}
		e.printStackTrace();
		System.out.println(buf.toString());
		return buf;
	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#addDevice()}.
	 */
	@Test
	public void testCreateManyAnonymousDevice() throws DevFailed {
		Session session = new Session();

		try {
			int expected = 5;
			String[] classes = new String[expected];

			Arrays.fill(classes, "TangoTest");

			List<Device> tangotests = session.addDevice(classes);

			assertNotNull(tangotests);
			assertEquals(expected, tangotests.size());

			// White Box
			for (Configuration conf : session.configurations) {
				for (Server server : conf) {
					assertEquals(expected, server.getChildren().size());
				}
			}

			session.create().start();

			for (Device d : tangotests) {
				assertNotNull(d);
				DeviceAttribute attr = d.getProxy().read_attribute(
						"double_scalar");
				attr.extractDouble();
				assertTrue("Device" + d.getProxy().name()
						+ " was created and it works", true);
			}

		} catch (DevFailed e) {
			assertTrue(printStackTrace(e).toString(), false);
		} finally {
			// It should be automatic
			session.init();
		}
	}

	/**
	 * Test method for {@link fr.soleil.tangounit.Session#addDevice()}.
	 */
	@Test
	public void testCreateManyDevice() throws DevFailed {
		Session session = new Session();

		try {
			int expected = 5;
			String[] classes = new String[expected];
			String[] devices = new String[expected];

			// x devices from TAngoTest classes
			Arrays.fill(classes, "TangoTest");

			// x devices name test/tangounit/X.i
			for (int i = 0; i < devices.length; i++) {
				devices[i] = "test/tangounit/X." + i;
			}

			List<Device> tangotests = session.addQualifiedDevice(classes,
					devices);

			session.create().start();

			assertNotNull(tangotests);
			assertEquals(expected, tangotests.size());

			// White Box
			for (Configuration conf : session.configurations) {
				for (Server server : conf) {
					assertEquals(expected, server.getChildren().size());
				}
			}

			for (Device device : tangotests) {
				assertNotNull(device);
				DeviceAttribute attr = device.getProxy().read_attribute(
						"double_scalar");
				attr.extractDouble();
				assertTrue("Device" + device.getProxy().name()
						+ " was created and it works", true);
			}

		} catch (DevFailed e) {
			assertTrue(printStackTrace(e).toString(), false);
		} finally {
			// It should be automatic
			session.init();
		}
	}

	/**
	 * Example : Test Memory in TangORB for reading attribute
	 * 
	 * @throws DevFailed
	 */
	@Test
	public void testMultipleReadOnOneDevice() throws DevFailed {
		Session session = new Session();
		try {

			Device tt = session.addDevice("TangoTest");
			session.create().start();
			for (int j = 0; j < 20; j++) {
				try {
					tt.getProxy().read_attribute("double_image");
				} catch (Exception e) {
					assertTrue("Use case failed²", true);
				}
			}
		} finally {
			session.init();
		}

	}
}
