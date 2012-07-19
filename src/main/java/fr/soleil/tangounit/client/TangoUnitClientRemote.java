/**
 * 
 */
package fr.soleil.tangounit.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.esrf.TangoDs.TangoConst;
import fr.esrf.TangoDs.Util;
import fr.soleil.api.Element;
import fr.soleil.tango.util.WaitStateUtilities;
import fr.soleil.tangounit.device.Device;
import fr.soleil.tangounit.device.Factories;

/**
 * @author HARDION
 * 
 */
public class TangoUnitClientRemote extends Element implements TangoUnitClient {

	private static final Logger logger = Logger
			.getLogger(TangoUnitClientRemote.class.getName());
	public static final long TIMEOUT = 30000;
	public static final long POLLING = 500;
	Thread executable = null;
	DeviceProxy session = null;

	/**
	 * @param clazz
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#addDevice(java.lang.String)
	 */
	public Device addDevice(String clazz) throws DevFailed {
		DeviceData data = new DeviceData();
		data.insert(new String[] { clazz });
		data = session.command_inout("addDevice", data);
		return Factories.getDevice(data.extractString(), clazz);
	}

	/**
	 * @param clazz
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#addTestDevice(java.lang.String)
	 */
	public Device addTestDevice(String clazz) throws DevFailed {
		DeviceData data = new DeviceData();
		data.insert(clazz);
		data = session.command_inout("addTestDevice", data);
		return Factories.getDevice(data.extractString(), clazz);
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#clear()
	 */
	public TangoUnitClient clear() throws DevFailed {
		session.command_inout("Clear");
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#create()
	 */
	public TangoUnitClient create() throws DevFailed {
		session.command_inout("Create");
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#delete()
	 */
	public TangoUnitClient delete() throws DevFailed {
		session.command_inout("Delete");
		return this;
	}

	/**
	 * @param programs
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#setTestServer(java.lang.String)
	 */
	public String setTestServer(String programs) throws DevFailed {
		DeviceAttribute data = new DeviceAttribute("testServer");
		data.insert(programs);
		session.write_attribute(data);
		data = session.read_attribute("testServer");

		return data.extractString();
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @throws TimeoutException
	 * @see fr.soleil.tangounit.Session#start()
	 */
	public TangoUnitClient start() throws DevFailed, TimeoutException {
		// if (logger.isDebugEnabled())
		logger.info("start - in");
		session.command_inout("Start");
		logger.info("starting devices for: " + session.get_device().name());
		WaitStateUtilities.failIfWrongStateAfterWhileState(session, DevState.ON,
				DevState.MOVING, TIMEOUT, POLLING);
		// if (logger.isDebugEnabled())
		logger.info("start - out");
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @throws TimeoutException
	 * @see fr.soleil.tangounit.Session#stop()
	 */
	public TangoUnitClient stop() throws DevFailed, TimeoutException {
		logger.info("stop - in");
		session.command_inout("Stop");
		WaitStateUtilities.failIfWrongStateAfterWhileState(session, DevState.OFF,
				DevState.MOVING, TIMEOUT, POLLING);
		logger.info("stop - out");
		return this;
	}

	Device testDevice = null;

	protected TangoUnitClientRemote(DeviceProxy proxy) throws DevFailed {
		super();
		ApiUtil.get_db_obj().setAccessControl(TangoConst.ACCESS_WRITE);
		// Other Client should use TangoUnit client API
		session = proxy;
		session.set_timeout_millis(30000);
		System.out.println("TangoUnitClientRemote use " + session.get_name());

	}

	public void executeTestDevice() throws DevFailed {
		DeviceAttribute attr = session.read_attribute("testDevices");
		String[] testDevices = attr.extractStringArray();

		if ((testDevices == null) || (testDevices.length == 0)) {
			throw new DevFailed("Why test a server without device",
					new DevError[0]);
		}

		String server = session.read_attribute("testServer").extractString();
		final String[] serverInfo = server.split("/");

		executable = new Thread(new Runnable() {

			public void run() {
				String[] args = new String[] { serverInfo[1] };
				try {
					Util tg = Util.init(args, serverInfo[0]);
					tg.server_init();
					System.out.println("Ready to accept request");
					tg.server_run();
				} catch (OutOfMemoryError ex) {
					System.err.println("Can't allocate memory !!!!");
					System.err.println("Exiting");
				} catch (UserException ex) {
					Except.print_exception(ex);
					System.err.println("Received a CORBA user exception");
					System.err.println("Exiting");
				} catch (SystemException ex) {
					Except.print_exception(ex);
					System.err.println("Received a CORBA system exception");
					System.err.println("Exiting");
				}
			}
		});
		executable.start();
		// take a Rest
		// XXX Test device has only 5 second to start !!!!!
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

	}

	public DeviceProxy getProxy(String devicename) throws DevFailed {
		throw new UnsupportedOperationException();
	}

	protected DeviceProxy getSession() {
		return session;
	}

	public void addDeviceProperties(String device, String property,
			String... values) throws DevFailed {
		DeviceData argin = new DeviceData();

		List<String> list = new ArrayList<String>(Arrays.asList(device));
		list.add(property);

		list.addAll(Arrays.asList(values));
		String[] array = list.toArray(new String[list.size()]);
		argin.insert(array);
		session.command_inout("AddDeviceProperties", argin);

	}
}
