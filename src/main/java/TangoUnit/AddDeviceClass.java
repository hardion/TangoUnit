//+======================================================================
// $Source: /usr/local/CVS/DeviceServer/Generic/Quality/TangoUnit/src/main/java/TangoUnit/AddDeviceClass.java,v $
//
// Project:      Tango Device Server
//
// Description:  Java source code for the command TemplateClass of the
//               TangoUnit class.
//
// $Author: hardion $
//
// $Revision: 1.3 $
//
// $Log: AddDeviceClass.java,v $
// Revision 1.3  2009/01/22 16:31:35  hardion
// * add asynchronous mode
// * fix release of device controlled by the factory
//
// Revision 1.2  2009/01/21 15:17:39  hardion
// * try to add a factory for Tangounit device creation bug:11454
// * finish implementation of remote client
//
// Revision 1.1  2008/12/05 18:27:08  hardion
// Version Alpha
//
//
// copyleft :    European Synchrotron Radiation Facility
//               BP 220, Grenoble 38043
//               FRANCE
//
//-======================================================================
//
//  		This file is generated by POGO
//	(Program Obviously used to Generate tango Object)
//
//         (c) - Software Engineering Group - ESRF
//=============================================================================



/**
 * @author	$Author: hardion $
 * @version	$Revision: 1.3 $
 */
package TangoUnit;



import org.omg.CORBA.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoDs.*;

/**
 *	Class Description:
 *	1arg to 5args : Class, [ [Server | Platform] [Instance]
 *	[Device]]\nClass : define the class of
 *	device.(required)\nServer : define the host where the
 *	DeviceServer have to run (*)(**)\nPlatform : define the
 *	platform where the DeviceServer have to run (*)(**)\nInstance
 *	: define the name of the server i.e "MyInstance" in
 *	ds_ControlBox/MyInstance (*)\nDevice : define the name of the
 *	device (*)(***)\n\n\n
*/


public class AddDeviceClass extends Command implements TangoConst
{
	//===============================================================
	/**
	 *	Constructor for Command class AddDeviceClass
	 *
	 *	@param	name	command name
	 *	@param	in	argin type
	 *	@param	out	argout type
	 */
	//===============================================================
	public AddDeviceClass(String name,int in,int out)
	{
		super(name, in, out);
	}

	//===============================================================
	/**
	 *	Constructor for Command class AddDeviceClass
	 *
	 *	@param	name            command name
	 *	@param	in              argin type
	 *	@param	in_comments     argin description
	 *	@param	out             argout type
	 *	@param	out_comments    argout description
	 */
	//===============================================================
	public AddDeviceClass(String name,int in,int out, String in_comments, String out_comments)
	{
		super(name, in, out, in_comments, out_comments);
	}
	//===============================================================
	/**
	 *	Constructor for Command class AddDeviceClass
	 *
	 *	@param	name            command name
	 *	@param	in              argin type
	 *	@param	in_comments     argin description
	 *	@param	out             argout type
	 *	@param	out_comments    argout description
	 *	@param	level           The command display type OPERATOR or EXPERT
	 */
	//===============================================================
	public AddDeviceClass(String name,int in,int out, String in_comments, String out_comments, DispLevel level)
	{
		super(name, in, out, in_comments, out_comments, level);
	}
	//===============================================================
	/**
	 *	return the result of the device's command.
	 */
	//===============================================================
	public Any execute(DeviceImpl device,Any in_any) throws DevFailed
	{
		Util.out2.println("AddDeviceClass.execute(): arrived");
		String[] argin = extract_DevVarStringArray(in_any);
		return insert(((TangoUnit)(device)).add_device(argin));
	}

	//===============================================================
	/**
	 *	Check if it is allowed to execute the command.
	 */
	//===============================================================
	public boolean is_allowed(DeviceImpl device, Any data_in)
	{
		if (device.get_state() == DevState.OFF  ||
			device.get_state() == DevState.ON  ||
			device.get_state() == DevState.MOVING)
		{
			//	End of Generated Code

			//	Re-Start of Generated Code
			return false;
		}
		return true;
	}
}
//-----------------------------------------------------------------------------
/* end of $Source: /usr/local/CVS/DeviceServer/Generic/Quality/TangoUnit/src/main/java/TangoUnit/AddDeviceClass.java,v $ */
