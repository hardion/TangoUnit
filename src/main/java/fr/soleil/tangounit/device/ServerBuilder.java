/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.soleil.tangounit.device;

import fr.soleil.api.IBuilder;
import fr.soleil.api.factory.Factory;

/**
 *
 * A builder for current pattern of DeviceServer : One program that contains One or more independant classes.
 * 
 * Not like GalilAxis
 * 
 * @author hardion
 */
public class ServerBuilder implements IBuilder<Server>{

    protected String program="NO_PROGRAM_DEFINED";
    
    ServerBuilder(Factory<Server> dsf, String program, String... classes  ) {
        this.program = program;
        for (int i = 0; i < classes.length; i++) {
            dsf.register(classes[i], this);
        }
    }

    public Server newInstance() {
        Server result = new Server();
        result.setName(program);        
        return result;
    }
}
