package fr.soleil.tangounit.device;


import java.util.ArrayList;
import java.util.List;

import fr.soleil.api.composite.Composite;

/**
 * Define a DeviceServer that may contains device
 * 
 * @author abeille
 * @author hardion
 */
public class Server extends Composite<Device> {
	
	public Server(){
		super();
	}
	
	@Override
	public String toString() {
		StringBuffer buf =  new StringBuffer();
		buf.append("serverName: "+ this.getName() +"\n");
		buf.append("deviceManagerList: "+ this +"\n");
		return buf.toString();
	}
	
    /**
	 * @return the program
	 */
	public String getProgram() {
		return ((ServerID)this.id).program;
	}

	/**
	 * @return the instance
	 */
	public String getInstance() {
		return ((ServerID)this.id).instance;
	}

	public List<String> getManagedClasses(){
		List<String> result = new ArrayList<String>(1);
		
		for (Device d : this.children) {
			String c = d.getClassName();
			if(!result.contains(c)){
				result.add(c);
			}
		}
		
		return result;
	}

}