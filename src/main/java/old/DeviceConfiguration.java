package old;

import java.util.HashMap;
import java.util.Map;

public class DeviceConfiguration {

	private String deviceName;
	private String className;
	
	
	private Map<String, String> devicePropertiesMap = new HashMap<String, String>();
	
	public DeviceConfiguration(String deviceName, String className) {
		this.deviceName = deviceName;
		this.className = className;		
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public String getClassName() {
		return className;
	}
	
	
	public Map<String, String> getDevicePropertiesMap() {
		return devicePropertiesMap;
	}
	public void setDevicePropertiesMap(Map<String, String> devicePropertiesMap) {
		this.devicePropertiesMap = devicePropertiesMap;
	}
	public void addDeviceProperty(String propertyName, String value) {
		devicePropertiesMap.put(propertyName, value);
	}

	
	
	
	
	
}
