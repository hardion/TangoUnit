package fr.soleil.tangounit.client;

import java.util.concurrent.TimeoutException;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceProxy;
import fr.soleil.tangounit.device.Device;

public interface TangoUnitClient {

	/**
	 * @param clazz
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#addDevice(java.lang.String)
	 */
	public Device addDevice(String clazz) throws DevFailed;

	/**
	 * Add properties for a given device
	 * 
	 * @param device
	 *            Device Name
	 * @param property
	 * @param properties
	 *            ["Property 1","Value 1", ...]
	 * @throws DevFailed
	 */
	public void addDeviceProperties(String device, String property,
			String... properties) throws DevFailed;

	/**
	 * @param clazz
	 * @return
	 * @see fr.soleil.tangounit.Session#addTestDevice(java.lang.String)
	 */
	public Device addTestDevice(String clazz) throws DevFailed;

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#clear()
	 */
	public TangoUnitClient clear() throws DevFailed;

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#create()
	 */
	public TangoUnitClient create() throws DevFailed;

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#delete()
	 */
	public TangoUnitClient delete() throws DevFailed;

	/**
	 * @param programs
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#setTestServer(java.lang.String)
	 */
	public String setTestServer(String programs) throws DevFailed;

	/**
	 * @return
	 * @throws DevFailed
	 * @throws TimeoutException
	 * @see fr.soleil.tangounit.Session#start()
	 */
	public TangoUnitClient start() throws DevFailed, TimeoutException;

	/**
	 * @return
	 * @throws DevFailed
	 * @see fr.soleil.tangounit.Session#stop()
	 */
	public TangoUnitClient stop() throws DevFailed, TimeoutException;

	public void executeTestDevice() throws DevFailed;

	public DeviceProxy getProxy(String devicename) throws DevFailed;

}
