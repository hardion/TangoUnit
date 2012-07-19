package old;

import java.util.ArrayList;
import java.util.List;


public class ServerConfiguration {

	private String serverName;
	private boolean isMock;
	private List<DeviceConfiguration> deviceConfigurationList = new ArrayList<DeviceConfiguration>();
	
	public ServerConfiguration(String serverName, boolean isMock) {
		this.serverName = serverName;
		this.isMock = isMock;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public boolean isMock() {
		return isMock;
	}
	public void setMock(boolean isMock) {
		this.isMock = isMock;
	}
	
	public void addDeviceToServer(DeviceConfiguration conf) {
		deviceConfigurationList.add(conf);
	}
}
