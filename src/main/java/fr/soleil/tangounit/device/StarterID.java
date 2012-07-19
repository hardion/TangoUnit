/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.soleil.tangounit.device;

import fr.soleil.api.Id;
import fr.soleil.tangounit.device.Starter.Platform;

/**
 *
 * @author hardion
 */
public class StarterID extends Id{
    protected final static String TYPE="STARTER";

    protected Platform platform = null;
    
    StarterID(String server, Platform platform) {
        super(server, TYPE);
        this.platform = platform;
    }

}
