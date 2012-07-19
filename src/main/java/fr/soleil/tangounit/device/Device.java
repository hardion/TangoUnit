package fr.soleil.tangounit.device;

import java.util.ArrayList;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceProxy;
import fr.soleil.api.Element;
import fr.soleil.api.IId;
import fr.soleil.api.composite.IComponent;
import fr.soleil.api.list.ElementList;
import fr.soleil.api.list.IElementList;

/**
 * Define a device for TangoUnit framework. It's a composite object in
 * "Composite" Pattern
 * 
 * Name of device is not mutable
 * 
 * 
 * @author ABEILLE
 * @author HARDION
 * 
 */
public class Device extends Element implements IComponent<Property> {

	private Class clazz;
	private DeviceProxy proxy;
	private final ElementList<Property> properties = new ElementList<Property>(
			new ArrayList<Property>());
	private final ElementList<Device> subproxies = new ElementList<Device>();

	/**
	 * @return the subproxies
	 */
	public ElementList<Device> getSubproxies() {
		return subproxies;
	}

	/**
	 * @return the properties
	 */
	public ElementList<Property> getProperties() {
		return properties;

	}

	/**
	 * Build a device
	 * 
	 */
	public Device() {
		super();
	}

	@Override
	public void setId(IId id) {
		if (!id.equals(this.id)) {
			super.setId(id);

			super.setName(id.getName());

			if (id instanceof DeviceID) {
				clazz = new Class();
				clazz.setName(((DeviceID) id).clazz);
			}
		}
	}

	/**
	 * Get DeviceProxy corresponding to the name. (Created on-the-fly)
	 * 
	 * @return the proxy
	 * @throws DevFailed
	 *             if building of proxy failed
	 */
	public DeviceProxy getProxy() throws DevFailed {
		if ((proxy == null) || !name.equals(proxy.get_name())) {
			proxy = new DeviceProxy(name);

		}
		return proxy;
	}

	/**
	 * Immuable (define by constructor
	 * 
	 * @see fr.soleil.api.GenericListModel#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
	}

	public String getClassName() {
		return clazz.getName();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("deviceName: " + this.getName() + "\n");
		buf.append("className: " + clazz.getName() + "\n");
		buf.append("properties: " + properties + "\n");
		return buf.toString();
	}

	public IElementList<Property> getChildren() {
		return properties;
	}
}
