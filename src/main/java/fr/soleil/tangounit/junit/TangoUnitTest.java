package fr.soleil.tangounit.junit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoDs.Except;
import fr.soleil.tangounit.client.TangoUnitClient;
import fr.soleil.tangounit.client.TangoUnitFactory;
import fr.soleil.tangounit.client.TangoUnitFactory.MODE;

public class TangoUnitTest {

	protected static TangoUnitClient tangounit;

	@BeforeClass
	public static void init() throws Exception {
		System.setProperty("TANGO_HOST", "tangodb:20001,tangodb:20002");

		try {
			ApiUtil.get_db_obj().set_timeout_millis(10000);
			tangounit = TangoUnitFactory.instance().createTangoUnitClient(
					MODE.remote);

			System.out.println("init done");
		} catch (DevFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Except.print_exception(e);
			Assert.fail();

		}
	}

	@AfterClass
	public static void release() throws Exception {
		TangoUnitFactory.instance().releaseTangoUnitClient(tangounit);
	}

}
