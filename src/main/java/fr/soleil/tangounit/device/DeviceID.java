/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.soleil.tangounit.device;

import fr.soleil.api.Id;

/**
 *
 * @author hardion
 */
public class DeviceID extends Id {

	public static final String TYPE = "Device";
    public String clazz = "";
    
    DeviceID(String device, String clazz) {
        super(device, TYPE);
        this.clazz = clazz;
    }

}
