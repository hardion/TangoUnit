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
public class ServerID extends Id{
    protected final static String TYPE="DEVICESERVER";
    
    protected String program = "";
    /**
	 * @return the program
	 */
	public String getProgram() {
		return program;
	}

	/**
	 * @return the instance
	 */
	public String getInstance() {
		return instance;
	}

	protected String instance = "";

    ServerID(String program, String instance) {
        super(program+"/"+instance, TYPE);
        this.program = program;
        this.instance = instance;
    }

}
