/**
 * 
 */
package fr.soleil.tangounit.device;

import java.util.HashMap;
import java.util.Iterator;

import fr.esrf.Tango.DevFailed;
import fr.soleil.api.factory.Factory;
import fr.soleil.api.flyweight.Flyweight;
import fr.soleil.api.list.ElementList;
import fr.soleil.tangounit.device.Starter.Platform;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author HARDION
 *@TODO singleton
 */
public class Factories {

	public final static Factory<Server> DS = new Factory<Server>();
	public final static Factory<Device> D = new Factory<Device>();
	protected final static Flyweight<Starter> Starters = new Flyweight<Starter>();

	/**
	 * @return the starters
	 */
	public static ElementList<Starter> getStarters() {
		return Starters;
	}

	public static HashMap<String, String> programs = new HashMap<String, String>();

	static {
		// TODO set parameterizable ...
		programs.put("Publisher", "Publisher");
		programs.put("TangoTest", "TangoTest");
		programs.put("SimulatedMotor", "SimulatedMotor");
		programs.put("GalilAxis", "ds_ControlBox");
		programs.put("ControlBox", "ds_ControlBox");
		programs.put("TangoUnit", "TangoUnit");

		DS.register(ServerID.TYPE, Server.class);
		D.register(DeviceID.TYPE, Device.class);
		Starters.register(StarterID.TYPE, Starter.class);
    try {
      List<StarterID> currentStarters = Starter.retrieveAllStarters();
      for (StarterID starterId : currentStarters) {
         Starters.create(starterId);
      }

    } catch (DevFailed ex) {
      Logger.getLogger(Factories.class.getName()).log(Level.SEVERE, null, ex);
    }

	}

	private static StarterID createStarterID(String server, Platform p) {

		return new StarterID(server, p);
	}

	public static Device getDevice(String device, String clazz) {
		System.out
				.println("      Factories - Device : " + device + " " + clazz);
		return Factories.D.create(new DeviceID(device, clazz));
	}

	public static Server getServerFromClass(String clazz, String instance) {
		System.out.println("      Factories - Server : " + clazz + " instance "
				+ instance);
		return Factories.DS.create(new ServerID(programs.get(clazz), instance));
	}

	public static Server getServerFromPrograms(String programs, String instance) {
		System.out.println("      Factories - Server : " + programs + " "
				+ instance);
		return Factories.DS.create(new ServerID(programs, instance));
	}

	public static void addStarter(String device, Platform platform) {
		Factories.Starters.create(Factories.createStarterID(device, platform));
	}

	/**
	 * @throws DevFailed
	 * 
	 */
	public static void removeStarter(String device) {

		for (Iterator<Starter> it = Factories.Starters.iterator(); it.hasNext();) {
			Starter starter = it.next();

			if (starter.getName().equalsIgnoreCase(device)) {
				// // First stop servers
				// starter.stopDeviceServer();
				//					
				// // Delete Server from database and release it
				// for (Server server : starter.getChildren()) {
				// this.deleteDeviceServer(server);
				// Factories.DS.release(server);
				// }

				// Remove from list and release it
				it.remove();
				break;
			}
		}
	}

	public static void removeStarter(Starter starter) {
		Factories.Starters.remove(starter);

	}

}
