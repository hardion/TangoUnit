package fr.soleil.tangounit.client;

import java.util.concurrent.TimeoutException;

import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.Tango.ErrSeverity;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.TangoApi.DeviceProxy;
import fr.soleil.tango.util.WaitStateUtilities;

/**
 * @author HARDION
 * 
 */
public class TangoUnitFactory {

	private static TangoUnitFactory instance = null;

	private final DeviceProxy proxy;

	protected TangoUnitFactory() throws DevFailed {
		super();
		Database db = ApiUtil.get_db_obj();
		// try to find the singleton of TangoUnitFactory
		String[] devs = db.get_device_exported_for_class("TangoUnitFactory");

		if ((devs != null) && (devs.length > 0)) {
			// Get the first and unique TangounitFactory device
			proxy = new DeviceProxy(devs[0]);
		} else {
			throw new DevFailed(
					new DevError[] { new DevError(
							"No Tango Unit Factory defined or started in database",
							ErrSeverity.ERR,
							"The TangoUnit service need a factory that creates and start TangoUnit device",
							"TangoUnitFactory.init()") });
		}
	}

	public final static synchronized TangoUnitFactory instance()
			throws DevFailed {
		if (instance == null) {
			instance = new TangoUnitFactory();
		}
		return instance;
	}

	public static enum MODE {
		remote, locale
	};

	public TangoUnitClient createTangoUnitClient(MODE mode) throws DevFailed,
			TimeoutException {
		TangoUnitClient result = null;
		if (mode == MODE.remote) {
			DeviceProxy tangoUnit = this.createTangoUnit();
			result = new TangoUnitClientRemote(tangoUnit);

		} else if (mode == MODE.locale) {
			result = new TangoUnitClientLocal();
		}

		return result;
	}

	public void releaseTangoUnitClient(TangoUnitClient client) throws DevFailed {
		if (client instanceof TangoUnitClientRemote) {
			TangoUnitClientRemote result = (TangoUnitClientRemote) client;
			this.releaseTangoUnit(result.getSession().get_name());
		} else if (client instanceof TangoUnitClientLocal) {
			TangoUnitClientLocal result = (TangoUnitClientLocal) client;
			result.stop().delete();

		}
	}

	protected DeviceProxy createTangoUnit() throws DevFailed, TimeoutException {
		DeviceProxy result = null;

		DeviceData data = proxy.command_inout("CreateDevice");
		result = new DeviceProxy(data.extractString());
		WaitStateUtilities.waitForState(result, DevState.STANDBY, 20000);

		return result;
	}

	protected void releaseTangoUnit(String device) throws DevFailed {
		DeviceData argin = new DeviceData();
		argin.insert(device);
		proxy.command_inout("ReleaseDevice", argin);
	}
}
