package fr.soleil.tangounit.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceProxy;
import fr.esrf.TangoDs.Except;
import fr.soleil.tangounit.client.TangoUnitFactory.MODE;
import fr.soleil.tangounit.device.Device;

public class TangoUnitClientTest {

	static TangoUnitClient client;
	static DeviceProxy proxy;

	@Test
	public void readTangoTestDoubleScalar() {

		System.out.println("start reading");
		DeviceAttribute da;
		try {
			da = proxy.read_attribute("double_scalar");
			Assert.assertNotNull(da);
			System.out.println(da.extractDouble());
		} catch (DevFailed e) {
			Except.print_exception(e);
			Assert.fail();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		try {
			ApiUtil.get_db_obj().set_timeout_millis(10000);
			client = TangoUnitFactory.instance().createTangoUnitClient(
					MODE.remote);

			Device d = client.addDevice("TangoTest");

			client
					.addDeviceProperties(d.getName(), "AttributesList",
							"current");
			client.create().start();

			proxy = d.getProxy();
			System.out.println("init done");
		} catch (DevFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Except.print_exception(e);
			Assert.fail();

		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TangoUnitFactory.instance().releaseTangoUnitClient(client);
	}

}
