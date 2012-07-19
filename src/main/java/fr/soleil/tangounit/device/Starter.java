/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.soleil.tangounit.device;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.ErrSeverity;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DbDatum;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.soleil.api.composite.Composite;
import java.util.List;

/**
 * 
 * @author hardion
 * @author abeille
 * 
 */
public class Starter extends Composite<Server> {

	public enum Platform {
		linux, win32
	};
  
  /**
   * Scan the Tango Database to retrieve all exported starters
   * @return all exported devices
   * @throws DevFailed 
   */
  public static List<StarterID> retrieveAllStarters() throws DevFailed{
    List<StarterID> result= new ArrayList<StarterID>() ;
    
    try{
      String[] starters = ApiUtil.get_db_obj().get_device_exported_for_class("Starter");
      
      for (int i = 0; i < starters.length; i++) {
        String starter = starters[i];
        DbDatum property = ApiUtil.get_db_obj().get_device_property(starter, "StartDsPath");
        String[] paths = property.toStringArray();
        for (int j = 0; j < paths.length; j++) {
          String path = paths[j];
          if(path.matches("(.\\:\\\\|\\\\\\\\)")){ // local windows path or network path
            result.add(new StarterID(starter, Platform.win32));
          }else{
            result.add(new StarterID(starter, Platform.linux));
          }
        }

      }
    }catch(DevFailed e){
      Except.throw_exception(
					"Can't retrieve the list of exported starters.",
					"Check your Tango Configuration", "Starter.retrieveAllStarters()");
    }
    
    
    return result;
    
  }
  
	protected Device deviceProxy = null;
  
 

	public Starter() {
		super();
	}

	public Platform getPlatform() {
		return ((StarterID) id).platform;
	}

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.esrf.TangoApi.Connection#get_timeout_millis()
	 */
	public int get_timeout_millis() throws DevFailed {
		return this.getProxy().get_timeout_millis();
	}

	private Device getDeviceProxy() {
		if (deviceProxy == null) {
			deviceProxy = new Device();
			deviceProxy.setId(new DeviceID(this.getName(), "Starter"));
		}

		return deviceProxy;
	}

	private DeviceProxy getProxy() throws DevFailed {
		return this.getDeviceProxy().getProxy();
	}
  
  public boolean isEnabled(){
    boolean result=false;
    try {
      if(this.getProxy().get_info().exported ){
        this.getProxy().state();
        result = true;
      }
    } catch (DevFailed e) {
      //result=false;
    }
    
    return result;
  }

	/**
	 * @param millis
	 * @throws DevFailed
	 * @see fr.esrf.TangoApi.Connection#set_timeout_millis(int)
	 */
	public void set_timeout_millis(int millis) throws DevFailed {
		this.getProxy().set_timeout_millis(millis);
	}

	protected void stopAll() throws DevFailed {
		for (Server ds : children) {
			this.stop(ds);
		}
	}

	protected void startAll() throws DevFailed {
		for (Server ds : children) {
			this.start(ds);
		}
	}

	public synchronized void start(Server s) throws DevFailed {
		// Check if Server was registered before
		if (!this.getChildren().contains(s)) {
			Except.throw_exception(
					"This Server is not controlled by this starter",
					"Server : " + s.getName() + " / Starter : "
							+ this.getName(), "Starter.start()");
		}

		DeviceData argin = new DeviceData();
		argin.insert(s.getName());
		// XXX
		this.getProxy().ping();
		System.out.println("Begin Request starting Server " + s.getName()
				+ " at " + System.currentTimeMillis());

		// don't use isStopped because the first time
		// the server doestn't appear in StoppedServers attribute
		if (!this.isRunning(s.getName())) {
			this.getProxy().command_inout("DevStart", argin);
		} else {
			System.out.println("Server " + s.getName() + " already started");
		}

		// wait for the device to be started
		this.waitWhenServerIsListed(s, "RunningServers");

		for (Device device : s.getChildren()) {

			for (int i = 0; true; i++) {
				try {
					System.out.println("   Ping n°" + i + " of "
							+ device.getName() + " and server" + s.getName());
					device.getProxy().ping();
					break;
				} catch (DevFailed e) {
					if (i >= RETRY) {
						ArrayList<DevError> err = new ArrayList<DevError>(
								java.util.Arrays.asList(e.errors));
						err
								.add(new DevError(
										"Device sould be started but it is not reacheable",
										ErrSeverity.ERR, "Starter is "
												+ this.getName()
												+ " Server is " + s.getName()
												+ " Device is "
												+ device.getName(), ""));
						e.errors = err.toArray(new DevError[err.size()]);
						throw e;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
				}
			}

		}
		System.out.println("End Request starting Server " + s.getName()
				+ " at " + System.currentTimeMillis());

	}

	private static final int RETRY = 3;

	private void waitWhenServerIsListed(Server s, String stringSpectrumAttribute)
			throws DevFailed {
		long t0 = System.currentTimeMillis();
		boolean listed = false;

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		while (!listed) {
			listed = this.isServerListed(s.getName(), stringSpectrumAttribute);

			if (!listed && (System.currentTimeMillis() - t0 > 20000)) { // TimeOut
				throw new DevFailed("TimeOut when wait for listing "
						+ s.getName() + " in " + stringSpectrumAttribute,
						new DevError[0]);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
		;

		// XXX
		if (listed) {
			System.out.println("Server " + s.getName() + " listed in "
					+ (System.currentTimeMillis() - t0));
		}

	}

	/**
	 * @param s
	 * @param stringSpectrumAttribute
	 * @return
	 * @throws DevFailed
	 */
	private boolean isServerListed(String serverName,
			String stringSpectrumAttribute) throws DevFailed {
		DeviceAttribute attr = this.getProxy().read_attribute(
				stringSpectrumAttribute);
		String[] array = attr.extractStringArray();
		return ArrayUtils.contains(array, serverName);
	}

	public synchronized void stop(Server s) throws DevFailed {
		if (!this.getChildren().contains(s)) {
			Except.throw_exception(
					"This Server is not controlled by this starter",
					"Server : " + s.getName() + " / Starter : "
							+ this.getName(), "Starter.stop()");
		}

		DeviceData argin = new DeviceData();
		argin.insert(s.getName());
		System.out.println("Begin Request stopping Server " + s.getName()
				+ " at " + System.currentTimeMillis());
		if (this.isRunning(s.getName())) {
			try {
				this.getProxy().command_inout("DevStop", argin);
			} catch (DevFailed e) {
				for (DevError error : e.errors) {
					System.out.println(error.desc);
					System.out.println(error.origin);
					System.out.println(error.reason);
					System.out.println(error.severity);
				}
				// Double stopping action because the reliability of Starter
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
				}
				this.getProxy().command_inout("DevStop", argin);
			}
		} else {
			System.out.println("Server " + s.getName() + " already stopped");
		}

		this.waitWhenServerIsListed(s, "StoppedServers");

		System.out.println("End Request stopping Server " + s.getName()
				+ " at " + System.currentTimeMillis());
		System.out.println("");
	}

	public boolean isRunning(String serverName) throws DevFailed {
		return this.isServerListed(serverName, "RunningServers");
	}

	public boolean isStopped(String serverName) throws DevFailed {
		return this.isServerListed(serverName, "StoppedServers");
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("starterDeviceName: " + this.getName() + "\n");
		return buf.toString();
	}

}
