/**
 * 
 */
package fr.soleil.tangounit.device;

import fr.soleil.api.Element;

public class Property extends Element {
	private final String[] value;

	public Property(String name, String... value) {
		super();
		super.setName(name);
		this.value = value;
	}

	/**
	 * Immuable
	 * 
	 * @see fr.soleil.api.Element#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {

	}

	public String[] getValue() {
		return value;
	};

}