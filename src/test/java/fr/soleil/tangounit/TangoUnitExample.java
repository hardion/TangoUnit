package fr.soleil.tangounit;


public class TangoUnitExample {

	/*
	 * public static void main(String args[]) throws DevFailed {
	 * System.setProperty("TANGO_HOST", "tangodb:20001");
	 * ApiUtil.get_db_obj().setAccessControl(TangoConst.ACCESS_WRITE);
	 * 
	 * 
	 * 
	 * } try {
	 * 
	 * // 1 - create tangotest and start it String tangoTestDeviceName =
	 * "TangoUnit/tangotest/1"; Configuration tangoTestConf = new
	 * Configuration("TangoTest/TangoUnit");
	 * tangoTestConf.addDeviceToServer(tangoTestDeviceName, "TangoTest"); // NB:
	 * mock mode not yet implemented tangoTestConf.setMock(false);
	 * System.out.println("conf:\n"+tangoTestConf);
	 * 
	 * Facade tangotest =
	 * TangoUnitFactory.getInstance().createDeviceManager(tangoTestConf);
	 * System.out.println("server:\n"+tangotest.getServer());
	 * tangotest.initServer();
	 * 
	 * // 2 - create beamlinestatus and start it String bLStatusdeviceName =
	 * "TangoUnit/beamlinestatus/1"; Configuration beamlineStatusConf = new
	 * Configuration("BeamlineStatus/TangoUnit");
	 * beamlineStatusConf.addDeviceToServer(bLStatusdeviceName,
	 * "BeamlineStatus"); // NB: mock mode not yet implemented
	 * beamlineStatusConf.setMock(false);
	 * beamlineStatusConf.addDeviceProperty(bLStatusdeviceName
	 * ,"contextCondition", "test1>100");
	 * //beamlineStatusConf.addDeviceProperty(deviceName,"contextConditionList",
	 * "context1:test1=100");
	 * beamlineStatusConf.addDeviceProperty(bLStatusdeviceName
	 * ,"contextVariables", "test1:TangoUnit/tangotest/1/double_scalar_w");
	 * System.out.println("conf:\n"+beamlineStatusConf); Facade beamlineStatus =
	 * TangoUnitFactory.getInstance().createDeviceManager(beamlineStatusConf);
	 * System.out.println("server:\n"+beamlineStatus.getServer());
	 * beamlineStatus.initServer();
	 * 
	 * // 4 - tests on devices, here changing context DeviceProxy devTangoTest =
	 * tangotest.getDeviceProxy(tangoTestDeviceName); DeviceAttribute devattr =
	 * devTangoTest.read_attribute("double_scalar_w"); devattr.insert(300.0);
	 * devTangoTest.write_attribute(devattr);
	 * 
	 * DeviceAttribute contextValidity =
	 * beamlineStatus.getDeviceProxy(bLStatusdeviceName
	 * ).read_attribute("contextValidity");
	 * System.out.println("context is "+contextValidity.extractBoolean());
	 * 
	 * devattr.insert(10.0); devTangoTest.write_attribute(devattr);
	 * 
	 * contextValidity =
	 * beamlineStatus.getDeviceProxy(bLStatusdeviceName).read_attribute
	 * ("contextValidity");
	 * System.out.println("context is "+contextValidity.extractBoolean());
	 * 
	 * 
	 * }catch(DevFailed e) { Except.print_exception(e); } // kill and delete all
	 * devices TangoUnitFactory.getInstance().clearDeviceManagers(); }
	 */
}
