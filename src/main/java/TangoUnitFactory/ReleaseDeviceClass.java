//+======================================================================
// $Source: /usr/local/CVS/DeviceServer/Generic/Quality/TangoUnit/src/main/java/TangoUnitFactory/ReleaseDeviceClass.java,v $
//
// Project:      Tango Device Server
//
// Description:  Java source code for the command TemplateClass of the
//               TangoUnitFactory class.
//
// $Author: hardion $
//
// $Revision: 1.1 $
//
// $Log: ReleaseDeviceClass.java,v $
// Revision 1.1  2009/01/21 15:17:39  hardion
// * try to add a factory for Tangounit device creation bug:11454
// * finish implementation of remote client
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
 * @version	$Revision: 1.1 $
 */
package TangoUnitFactory;



import org.omg.CORBA.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoDs.*;

/**
 *	Class Description:
 *	It's very important to release a device after use because it can be reuse in place of build a new one.
*/


public class ReleaseDeviceClass extends Command implements TangoConst
{
	//===============================================================
	/**
	 *	Constructor for Command class ReleaseDeviceClass
	 *
	 *	@param	name	command name
	 *	@param	in	argin type
	 *	@param	out	argout type
	 */
	//===============================================================
	public ReleaseDeviceClass(String name,int in,int out)
	{
		super(name, in, out);
	}

	//===============================================================
	/**
	 *	Constructor for Command class ReleaseDeviceClass
	 *
	 *	@param	name            command name
	 *	@param	in              argin type
	 *	@param	in_comments     argin description
	 *	@param	out             argout type
	 *	@param	out_comments    argout description
	 */
	//===============================================================
	public ReleaseDeviceClass(String name,int in,int out, String in_comments, String out_comments)
	{
		super(name, in, out, in_comments, out_comments);
	}
	//===============================================================
	/**
	 *	Constructor for Command class ReleaseDeviceClass
	 *
	 *	@param	name            command name
	 *	@param	in              argin type
	 *	@param	in_comments     argin description
	 *	@param	out             argout type
	 *	@param	out_comments    argout description
	 *	@param	level           The command display type OPERATOR or EXPERT
	 */
	//===============================================================
	public ReleaseDeviceClass(String name,int in,int out, String in_comments, String out_comments, DispLevel level)
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
		Util.out2.println("ReleaseDeviceClass.execute(): arrived");
		String argin = extract_DevString(in_any);
		((TangoUnitFactory)(device)).release_device(argin);
		return insert();
	}

	//===============================================================
	/**
	 *	Check if it is allowed to execute the command.
	 */
	//===============================================================
	public boolean is_allowed(DeviceImpl device, Any data_in)
	{
			//	End of Generated Code

			//	Re-Start of Generated Code
		return true;
	}
}
//-----------------------------------------------------------------------------
/* end of $Source: /usr/local/CVS/DeviceServer/Generic/Quality/TangoUnit/src/main/java/TangoUnitFactory/ReleaseDeviceClass.java,v $ */
