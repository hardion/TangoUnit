/**
 * 
 */
package fr.soleil.tangounit.client;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.esrf.TangoDs.TangoConst;
import fr.esrf.TangoDs.Util;
import fr.soleil.api.Element;
import fr.soleil.tangounit.Session;
import fr.soleil.tangounit.device.Device;

/**
 * @author HARDION
 * 
 */
public class TangoUnitClientLocal extends Element implements TangoUnitClient {

	Thread executable = null;
	Session session = null;

	/**
	 * @param clazz
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#addDevice(java.lang.String)
	 */
	public Device addDevice(String clazz) throws DevFailed {
		return session.addDevice(clazz);
	}

	/**
	 * @param clazz
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#addTestDevice(java.lang.String)
	 */
	public Device addTestDevice(String clazz) throws DevFailed {
		return session.addTestDevice(clazz);
	}

	/**
	 * @return
	 * @see fr.soleil.tangounit.Session#clear()
	 */
	public TangoUnitClient clear() {
		session.clear();
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#create()
	 */
	public TangoUnitClient create() throws DevFailed {
		session.create();
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#delete()
	 */
	public TangoUnitClient delete() throws DevFailed {
		session.delete();
		return this;
	}

	/**
	 * @param programs
	 * @return
	 * @see fr.soleil.tangounit.Session#setTestServer(java.lang.String)
	 */
	public String setTestServer(String programs) {

		return session.setTestServer(programs).getName();
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#start()
	 */
	public TangoUnitClient start() throws DevFailed {
		session.start();
		return this;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#stop()
	 */
	public TangoUnitClient stop() throws DevFailed {
		session.stop();
		return this;
	}

	Device testDevice = null;

	public TangoUnitClientLocal() throws DevFailed {
		super();
		ApiUtil.get_db_obj().setAccessControl(TangoConst.ACCESS_WRITE);
		// Other Client should use TangoUnit client API
		session = new Session();

	}

	public void executeTestDevice() throws DevFailed {
		if (session.getTestServer().getChildren().size() == 0) {
			throw new DevFailed("Why test a server without device",
					new DevError[0]);
		}

		// get device of this class

		executable = new Thread(new Runnable() {

			public void run() {
				String[] args = new String[] { session.getTestServer()
						.getInstance() };
				try {
					for (String clazz : session.getTestServer()
							.getManagedClasses()) {
						Util tg = Util.init(args, clazz);
						tg.server_init();
						System.out.println("Ready to accept request");
						tg.server_run();
					}
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
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

	}

	public DeviceProxy getProxy(String devicename) throws DevFailed {
		throw new UnsupportedOperationException();
	}

	public void addDeviceProperties(String device, String property,
			String[] values) throws DevFailed {
		session.addDeviceProperty(device, property, values);

	}

}
