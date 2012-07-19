/**
 * 
 */
package fr.soleil.tangounit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DbDatum;
import fr.esrf.TangoApi.DbDevInfo;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.TangoDs.TangoConst;
import fr.soleil.api.Element;
import fr.soleil.api.list.ElementList;
import fr.soleil.tangounit.device.Device;
import fr.soleil.tangounit.device.Factories;
import fr.soleil.tangounit.device.Property;
import fr.soleil.tangounit.device.Server;
import fr.soleil.tangounit.device.Starter;
import fr.soleil.tangounit.device.Starter.Platform;

/**
 * Create a session for Tango Unit Framework
 * 
 * 1arg to 5args : Class, [ [Server | Platform] [Instance] [Device]] Class :
 * define the class of device.(required) Server : define the host where the
 * DeviceServer have to run (*)(**) Platform : define the platform where the
 * DeviceServer have to run (*)(**) Instance : define the name of the server i.e
 * "MyInstance" in ds_ControlBox/MyInstance (*) Device : define the name of the
 * device (*)(***)
 * 
 * @TODO Make a true Facade and separate configuration from engine
 * 
 * @author HARDION
 * @author ABEILLE
 * 
 */
public class Session extends Element {

	static {
		try {
			ApiUtil.get_db_obj().setAccessControl(TangoConst.ACCESS_WRITE);
		} catch (DevFailed e) {
			Logger.global.severe("can't set Database in ACCESS_WRITE mode");
			e.printStackTrace();
		}
	}

	// This list keeps the associativity between Starter and Server for this
	// session
	// We can't use a list of starter directly because this list from factory is
	// shared by all session
	protected class Configuration extends ElementList<Server> {

		public Configuration() {
			super(new HashSet<Server>());
		}

		Starter starter = null;
	}

	// This list keeps reference to starters which keep references on Server
	// which ... on Device
	protected ElementList<Configuration> configurations = new ElementList<Configuration>();
	// TODO Maybe it's better to keep servers from starter because starter can
	// be shared by other session

	// Server to test (associate with no host)
	// This session don't start these server
	protected Server testServer = null;

	public enum State {
		standby, on, off
	};

	public State state = State.standby;

	/**
	 * @return the testServer
	 */
	public Server getTestServer() {
		return testServer;
	}

	/**
	 * @param o
	 * @return
	 */
	public Server setTestServer(String programs) {
		return this.setTestServer(programs, "TangoUnit.TestServer");
	}

	public Session createTestDevice(String device) throws DevFailed {

		for (Device d : testServer.getChildren()) {
			if (device.equalsIgnoreCase(d.getName())) {
				this.createDevice(testServer, d);
			}
		}

		return this;
	}

	public Device deleteTestDevice(String device) throws DevFailed {
		Device result = null;
		for (Device d : testServer.getChildren()) {
			if (device.equalsIgnoreCase(d.getName())) {
				this.deleDeviceFromDb(d);
				result = d;
				break;
			}
		}

		return result;
	}

	/**
	 * @param programs
	 * @param instance
	 * @return
	 */
	public Server setTestServer(String programs, String instance) {
		if (testServer != null) { // TODO test also equality of programs

			for (Device device : testServer.getChildren()) {
				Factories.D.release(device);
			}
			testServer.getChildren().clear();
			Factories.DS.release(testServer);
		}

		if (programs != null) {
			testServer = Factories.getServerFromPrograms(programs, instance);
		} else {
			testServer = null;
		}

		return testServer;
	}

	public Device addTestDevice(String clazz) {
		Device result = null;
		if (testServer != null) {
			result = Factories.getDevice("Test/TangoUnit/TestDevice." + clazz
					+ "." + Math.random(), clazz);
			testServer.getChildren().add(result);
		}

		return result;
	}

	public Device removeTestDevice(String device) {
		Device result = null;
		if (testServer != null) {
			for (Iterator<Device> it = testServer.getChildren().iterator(); it
					.hasNext();) {
				Device d = it.next();
				if (d.getName().equalsIgnoreCase(device)) {
					result = d;
					Factories.D.release(d);
					it.remove();
				}
			}
		}
		return result;
	}

	public Session() throws DevFailed {
		super();

		// TODO CompactPCICrate.GetOSVersion
	}

	/**
	 * 
	 */

	public Session create() throws DevFailed {
		if (testServer != null) {
			this.createServer(testServer);
		}
		for (Configuration st : configurations) {
			for (Server s : st) {
				try {
					this.createServer(s);
				} catch (DevFailed e) {
					s.setEnabled(false);
					throw e;
				}
			}
		}
		// TODO true state machine
		state = State.off;
		this.setEnabled(true);

		return this;
	}

	/**
	 * Start all device defined in this session. FOR ANY PROBLEM, the method
	 * stop is called
	 * 
	 * @return this session
	 * @throws DevFailed
	 *             when the starter can't start the Server
	 */
	public Session start() throws DevFailed {
		// Only start server from starter
		try {
			for (Configuration s : configurations) {
				for (Server server : s) {
					s.starter.start(server);
				}
			}

		} catch (DevFailed e) {
			this.stop();
			throw e;
		}
		// TODO true state machine
		state = State.on;
		this.setEnabled(true);

		return this;
	}

	public Session stop() throws DevFailed {

		try {
			for (Configuration s : configurations) {
				for (Server server : s) {
					s.starter.stop(server);
				}
			}
		} catch (DevFailed e) {
			throw e;
		}

		// take a Rest because the Starter comes from the South
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
		state = State.off;

		return this;
	}

	public Session delete() throws DevFailed {

		for (Configuration st : configurations) {
			for (Server s : st) {
				try {
					this.deleteDeviceServerFromDB(s);
				} catch (DevFailed e) {
					s.setEnabled(false);
					// TODO more robust because we won't delete other server !!!
					throw e;
				}
			}
		}
		if (testServer != null) {
			this.deleteAliveDeviceServerFromDB(testServer);
		}

		state = State.standby;
		return this;
	}

	protected void createServer(Server s) throws DevFailed {
		for (Device d : s.getChildren()) {
			this.createDevice(s, d);
		}
		s.setEnabled(true);
	}

	protected void createDevice(Server s, Device d) throws DevFailed {
		// TODO: check if already exist, because does not throw exc
		DbDevInfo dinfo = new DbDevInfo(d.getName(), d.getClassName(), s
				.getName());
		ApiUtil.get_db_obj().add_device(dinfo);

		this.createDeviceProperties(d);
		d.setEnabled(true);

	}

	protected void createDeviceProperties(Device d) throws DevFailed {
		DbDatum[] datum = new DbDatum[d.getProperties().size()];
		int i = 0;
		for (Property property : d.getProperties()) {
			datum[i++] = new DbDatum(property.getName(), property.getValue());
		}
		ApiUtil.get_db_obj().put_device_property(d.getName(), datum);
		d.getProperties().setEnabled(true);
	}

	protected void deleteDeviceProperties(Device d) throws DevFailed {
		DbDatum[] datum = new DbDatum[d.getProperties().size()];
		int i = 0;
		for (Property property : d.getProperties()) {
			datum[i++] = new DbDatum(property.getName());
		}
		ApiUtil.get_db_obj().delete_device_property(d.getName(), datum);
		d.getProperties().setEnabled(false);
	}

	protected void deleteDeviceServerFromDB(Server s) throws DevFailed {
		for (Device d : s.getChildren()) {
			this.deleDeviceFromDb(d);
		}
		try {
			ApiUtil.get_db_obj().delete_server(s.getName());
		} catch (DevFailed e) {
			// We can continue
			// There is no good reason to stop the process
			System.out.println(s.getName() + " doesn't exist in database");

		}
		s.setEnabled(false);
	}

	protected void deleDeviceFromDb(Device d) throws DevFailed {
		this.deleteDeviceProperties(d);
		// TODO: check if already exist, because does not throw exc
		// Don't force deleting by another way
		// i.e directly with
		// ApiUtil.get_db_obj().command_inout("DbDeleteDevice", data);
		ApiUtil.get_db_obj().delete_device(d.getName());
		d.setEnabled(false);
	}

	protected void deleteAliveDeviceServerFromDB(Server s) throws DevFailed {
		for (Device d : s.getChildren()) {
			this.deleteDeviceProperties(d);
			// TODO: check if already exist, because does not throw exc
			// force deleting
			DeviceData data = new DeviceData();
			data.insert(d.getName());
			ApiUtil.get_db_obj().command_inout("DbDeleteDevice", data);
			d.setEnabled(false);
		}
		DeviceData data = new DeviceData();
		data.insert(s.getName());
		ApiUtil.get_db_obj().command_inout("DbDeleteServer", data);
		s.setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	public Session clear() {
		for (Iterator<Configuration> iterator = configurations.iterator(); iterator
				.hasNext();) {
			Configuration starterServer = iterator.next();

			// don't free reference like this
			// starter.getChildren().clear();
			// but think it can be reusable
			for (Server server : starterServer) {
				for (Device device : server.getChildren()) {
					Factories.D.release(device);
				}
				server.getChildren().clear();
				Factories.DS.release(server);
			}
		}
		configurations.clear();

		// Test DEvices
		this.setTestServer(null, null);

		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// Delete Devices
		// this.stop();
		// this.delete();
		// super.finalize();
	}

	public List<Device> addDevice(String[] classes) {
		List<Device> result = new ArrayList<Device>();
		for (int i = 0; i < classes.length; i++) {
			result.add(this.addDevice(classes[i]));
		}
		return result;
	}

	public List<Device> addQualifiedDevice(String[] classes, String[] devices) {
		List<Device> result = new ArrayList<Device>();
		for (int i = 0; i < classes.length; i++) {
			result.add(this.addQualifiedDevice(classes[i], devices[i]));
		}
		return result;
	}

	public Device addDevice(String clazz, String host, String instance,
			String device) {

		Starter s = this.getStarter(host);

		Server ds = Factories.getServerFromClass(clazz, instance);

		Device d = Factories.getDevice(device, clazz);

		s.getChildren().add(ds);
		ds.getChildren().add(d);

		this.addStarterServer(s, ds);

		return d;
	}

	private void addStarterServer(Starter starter, Server server) {
		Configuration result = null;
		for (Configuration starterServer : configurations) {
			// Here we can compare reference because each starter is unique
			if (starterServer.starter == starter) {
				result = starterServer;
				break;
			}
		}

		if (result == null) {
			result = new Configuration();
			result.starter = starter;
			configurations.add(result);
		}
		// Don't need to check existence because inner list
		// is an HashSet
		result.add(server);
	}

	private Starter getStarter(String host) {
		Starter result = null;
		for (Starter starter : Factories.getStarters()) {
			if (starter.getName().equalsIgnoreCase(host)) {
				result = starter;
				break;
			}
		}
		return result;
	}

	public Device addDevice(String clazz, String host, String instance) {
		return this.addDevice(clazz, host, instance, "Test/TangoUnit/" + clazz
				+ "." + Math.random());
	}

	public Device addDevice(String clazz, String host) {
		// Check if server designs a platform
		for (Platform platform : Platform.values()) {
			if (platform.toString().equalsIgnoreCase(host)) {
				host = this.getStarter(platform).getName();
				break;
			}
		}

		return this.addDevice(clazz, host, "TangoUnit."
				+ Session.this.hashCode());
	}

	public Device addDevice(String clazz, Platform platform) {

		Starter s = this.getStarter(platform);

		return this.addDevice(clazz, s.getName());
	}

	/**
	 * @param platform
	 * @return
	 * @throws Exception
	 */
	protected Starter getStarter(Platform platform) {
		Starter result = null;
		// Define server for the target platform
		for (Starter s : Factories.getStarters()) {
			// find the first define for this platform
			if (s.getPlatform().equals(platform) && s.isEnabled()) {
				result = s;
				break;
			}
		}
		return result;
	}

	/**
	 * @param platform
	 * @return
	 * @throws Exception
	 */
	protected Starter getStarter() {
		// get default starter from linux first
		Starter s = this.getStarter(Platform.linux);
		// from win if any
		if (s == null) {
			s = this.getStarter(Platform.win32);
		}
		return s;
	}

	public Device addDevice(String clazz) {
		Starter s = this.getStarter();
		return this.addDevice(clazz, s.getName());
	}

	public Device addQualifiedDevice(String clazz, String Device) {
		return this.addDevice(clazz, this.getStarter().getName(), "TangoUnit."
				+ Session.this.hashCode(), Device);
	}

	protected Device getDevice(String name) {
		Device result = null;
		for (Configuration conf : configurations) {
			for (Server server : conf) {
				for (Device d : server.getChildren()) {
					if (d.getName().equalsIgnoreCase(name)) {
						result = d;
						break;
					}
				}
				if (result != null) {
					break;
				}
			}
			if (result != null) {
				break;
			}
		}

		// try with test device
		if (result == null) {
			for (Device d : testServer.getChildren()) {
				if (d.getName().equalsIgnoreCase(name)) {
					result = d;
					break;
				}
			}
		}

		return result;
	}

	public void addDeviceProperty(String device, String property, String... values ) throws DevFailed {
		Device d = this.getDevice(device);
		if (d == null) {
			DevError err = new DevError();
			err.reason = "Session.addDeviceProperties() : Device not found";
			throw new DevFailed(new DevError[] { err });
		}
		Property p = new Property(property, values);
		d.getProperties().add(p);
	}

	/**
	 * Get informations about all devices of this session
	 * 
	 * The array of String array is structured like this : [Name Of Device][Name
	 * of Class][Name of DeviceServer][Name of Starter][Platform of Starter] ...
	 * as many as device
	 * 
	 * @return An array of device information
	 */
	public String[][] getDevicesConfiguration() {
		List<String[]> devices = new ArrayList<String[]>();
		String[] inf = null;
		for (Configuration s : configurations) {
			for (Server server : s) {
				for (Device device : server.getChildren()) {
					inf = new String[5];
					inf[0] = device.getName();
					inf[1] = device.getClassName();
					inf[2] = server.getName();
					inf[3] = s.starter.getName();
					inf[4] = s.starter.getPlatform().toString();

					devices.add(inf);
				}
			}
		}
		int j = 0;
		j++;
		// transform in array of array
		String[][] result = new String[devices.size()][];
		int i = 0;
		for (String[] strings : devices) {
			result[i] = strings;
			i++;
		}

		return result;
	}

	/**
	 * Get informations about all test devices of this session
	 * 
	 * The array of String array is structured like this : [Name Of Device][Name
	 * of Class][Name of DeviceServer] ... as many as device
	 * 
	 * @return An array of device information
	 */
	public String[][] getTestDevicesConfiguration() {
		List<String[]> devices = new ArrayList<String[]>();
		if (testServer != null) {
			String[] inf = null;
			for (Device device : testServer.getChildren()) {
				inf = new String[3];
				inf[0] = device.getName();
				inf[1] = device.getClassName();
				inf[2] = testServer.getName();
				devices.add(inf);
			}
		}
		int j = 0;
		j++;
		// transform in array of array
		String[][] result = new String[devices.size()][];
		int i = 0;
		for (String[] strings : devices) {
			result[i] = strings;
			i++;
		}
		return result;
	}

	/**
	 * Transform a 2d Matrix to 1d array which has (m rows * n columns) elements
	 * 
	 * @param src
	 * @return
	 */
	public final static String[] flatten(String[][] src) {
		String[] dst = null;
		if ((src == null) || (src.length == 0)) {
			dst = new String[0];
		} else {
			int size = src.length * src[0].length;
			dst = new String[size];
			int length = 0;
			for (int i = 0; i < src.length; i++) {
				System.arraycopy(src[i], 0, dst, length, src[i].length);
				length += src[i].length;
			}
		}

		return dst;
	}

	public void init() throws DevFailed {
		if (state == State.on) {
			this.stop();
		}
		if (state == State.off) {
			this.delete();
		}
		if (state == State.standby) {
			this.clear();
		}
	}

}
